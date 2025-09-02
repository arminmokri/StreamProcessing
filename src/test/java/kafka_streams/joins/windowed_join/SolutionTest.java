package kafka_streams.joins.windowed_join;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SolutionTest {
    private static final String INPUT_TOPIC_A = Solution.APPLICATION_NAME + "_A" + "_input";
    private static final String INPUT_TOPIC_B = Solution.APPLICATION_NAME + "_B" + "_input";
    private static final String OUTPUT_TOPIC = Solution.APPLICATION_NAME + "_output";

    private static Solution solution;
    private static KafkaProducer<String, String> producer;
    private static KafkaConsumer<String, String> consumer;

    @BeforeAll
    public static void setup() throws Exception {
        solution = new Solution();

        // Clean up topics before starting
        Solution.deleteTopic(INPUT_TOPIC_A);
        Solution.deleteTopic(INPUT_TOPIC_B);
        Solution.deleteTopic(OUTPUT_TOPIC);
        Solution.createTopic(INPUT_TOPIC_A);
        Solution.createTopic(INPUT_TOPIC_B);
        Solution.createTopic(OUTPUT_TOPIC);

        // Start Kafka Streams
        solution.startStream(INPUT_TOPIC_A, INPUT_TOPIC_B, OUTPUT_TOPIC);

        // Init Producer
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Solution.BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(producerProps);

        // Init Consumer
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, Solution.BOOTSTRAP_SERVERS);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumer = new KafkaConsumer<>(consumerProps);

    }

    @AfterAll
    public static void cleanup() {
        if (producer != null) producer.close();
        if (consumer != null) consumer.close();

        solution.stopStream();
        Solution.deleteTopic(INPUT_TOPIC_A);
        Solution.deleteTopic(INPUT_TOPIC_B);
        Solution.deleteTopic(OUTPUT_TOPIC);
    }

    @Test
    public void testDefaultCase() {

        long baseTime = System.currentTimeMillis();

        // t=0s
        // Order(String id, List<String> items)
        sendInput(INPUT_TOPIC_A, "1000", "{\"id\": \"1000\", \"items\": [\"shoes\", \"bag\"]}", baseTime);

        // t=3s
        // Payment(String id, String orderId, Integer amount)
        sendInput(INPUT_TOPIC_B, "1000", "{\"id\": \"2000\", \"orderId\": \"1000\", \"amount\": 100}", baseTime + 3000);

        // t=6s
        sendInput(INPUT_TOPIC_A, "1001", "{\"id\": \"1001\", \"items\": [\"iPhone\", \"AirPods\"]}", baseTime + 6000);
        sendInput(INPUT_TOPIC_B, "1001", "{\"id\": \"2001\", \"orderId\": \"1001\", \"amount\": 2000}", baseTime + 6000);

        // t=7s
        sendInput(INPUT_TOPIC_A, "1002", "{\"id\": \"1002\", \"items\": [\"Galaxy\"]}", baseTime + 7000);

        // t=13s
        sendInput(INPUT_TOPIC_B, "1002", "{\"id\": \"2001\", \"orderId\": \"1002\", \"amount\": 500}", baseTime + 13000);


        Map<String, String> results = readOutput(OUTPUT_TOPIC, 2, 5_000);

        System.out.println("results=" + results);

        assertEquals(
                "{\"id\":\"1000\",\"items\":[\"shoes\",\"bag\"],\"paymentId\":\"2000\",\"amount\":100}",
                results.get("1000")
        );
        assertEquals(
                "{\"id\":\"1001\",\"items\":[\"iPhone\",\"AirPods\"],\"paymentId\":\"2001\",\"amount\":2000}",
                results.get("1001")
        );
        assertNull(
                results.get("1002")
        );

    }

    private void sendInput(String topic, String key, String value, Long timestamp) {

        ProducerRecord<String, String> record;
        if (Objects.isNull(timestamp)) {
            record = new ProducerRecord<>(topic, key, value);
        } else {
            record = new ProducerRecord<>(topic, null, timestamp, key, value);
        }

        producer.send(record);
        producer.flush();
    }

    private static Map<String, String> readOutput(String topic, int expectedKeys, long timeoutMillis) {

        consumer.subscribe(List.of(topic));

        Map<String, String> results = new LinkedHashMap<>();
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < timeoutMillis && results.size() < expectedKeys) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                results.put(record.key(), record.value());
            }
        }

        consumer.unsubscribe();

        return results;
    }
}
