package kafka_streams.joins.left_join;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        // Customer(String id, String name)
        sendInput(INPUT_TOPIC_A, "1000", "{\"id\": \"1000\", \"name\": \"Alice\"}");
        sendInput(INPUT_TOPIC_A, "1001", "{\"id\": \"1001\", \"name\": \"Bob\"}");

        // Order(String id, String customerId, List<String> items)
        sendInput(INPUT_TOPIC_B, "1000", "{\"id\": \"2000\", \"customerId\": \"1000\", \"items\": [\"iPhone\", \"AirPods\"]}");
        sendInput(INPUT_TOPIC_B, "1001", "{\"id\": \"2001\", \"customerId\": \"1001\", \"items\": [\"Galaxy\"]}");
        sendInput(INPUT_TOPIC_B, "1002", "{\"id\": \"2002\", \"customerId\": \"1002\", \"items\": [\"Video Player\"]}");

        Map<String, String> results = readOutput(OUTPUT_TOPIC, 2, 5_000);

        System.out.println("results=" + results);

        assertEquals(
                "{\"id\":\"2000\",\"customerId\":\"1000\",\"items\":[\"iPhone\",\"AirPods\"],\"name\":\"Alice\"}",
                results.get("1000")
        );
        assertEquals(
                "{\"id\":\"2001\",\"customerId\":\"1001\",\"items\":[\"Galaxy\"],\"name\":\"Bob\"}",
                results.get("1001")
        );
        assertEquals(
                "{\"id\":\"2002\",\"customerId\":\"1002\",\"items\":[\"Video Player\"],\"name\":\"unknown\"}",
                results.get("1002")
        );
    }

    private static void sendInput(String topic, String key, String value) {
        producer.send(new ProducerRecord<>(topic, key, value));
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
