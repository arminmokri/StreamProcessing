package kafka_streams.real_world_use_cases.clickstream_analytics;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SolutionTest {
    private static final String INPUT_TOPIC = Solution.APPLICATION_NAME + "_input";
    private static final String OUTPUT_TOPIC = Solution.APPLICATION_NAME + "_output";

    private static Solution solution;
    private static KafkaProducer<String, String> producer;
    private static KafkaConsumer<String, String> consumer;

    @BeforeAll
    public static void setup() throws Exception {
        solution = new Solution();

        // Clean up topics before starting
        Solution.deleteTopic(INPUT_TOPIC);
        Solution.deleteTopic(OUTPUT_TOPIC);
        Solution.createTopic(INPUT_TOPIC);
        Solution.createTopic(OUTPUT_TOPIC);

        // Start Kafka Streams
        solution.startStream(INPUT_TOPIC, OUTPUT_TOPIC);

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
        if (Objects.nonNull(producer)) {
            producer.close();
        }
        if (Objects.nonNull(consumer)) {
            consumer.close();
        }

        solution.stopStream();
        Solution.deleteTopic(INPUT_TOPIC);
        Solution.deleteTopic(OUTPUT_TOPIC);
    }

    @Test
    public void testDefaultCase() {

        // variable

        // ClickEvent(String userId, String page) - INPUT_TOPIC
        Serde<Solution.ClickEvent> clickEventSerde = Solution.getSerde(
                new TypeReference<Solution.ClickEvent>() {
                }
        );

        Solution.ClickEvent clickEvent1 = new Solution.ClickEvent("user1", "/home");
        Solution.ClickEvent clickEvent2 = new Solution.ClickEvent("user2", "/products");
        Solution.ClickEvent clickEvent3 = new Solution.ClickEvent("user1", "/home");
        Solution.ClickEvent clickEvent4 = new Solution.ClickEvent("user3", "/home");
        Solution.ClickEvent clickEvent5 = new Solution.ClickEvent("user2", "/products");

        // PageView(String page, Long count) - OUTPUT_TOPIC
        Serde<Solution.PageView> pageViewSerde = Solution.getSerde(
                new TypeReference<Solution.PageView>() {
                }
        );

        Solution.PageView pageView1 = new Solution.PageView("/home", 3L);
        Solution.PageView pageView2 = new Solution.PageView("/products", 2L);

        // test

        sendInput(INPUT_TOPIC, "user1", new String(clickEventSerde.serializer().serialize(null, clickEvent1)), null);
        sendInput(INPUT_TOPIC, "user2", new String(clickEventSerde.serializer().serialize(null, clickEvent2)), null);
        sendInput(INPUT_TOPIC, "user1", new String(clickEventSerde.serializer().serialize(null, clickEvent3)), null);
        sendInput(INPUT_TOPIC, "user3", new String(clickEventSerde.serializer().serialize(null, clickEvent4)), null);
        sendInput(INPUT_TOPIC, "user2", new String(clickEventSerde.serializer().serialize(null, clickEvent5)), null);

        List<ConsumerRecord<String, String>> results = readOutput(OUTPUT_TOPIC, 0, 5_000);

        String stringResult = results
                .stream()
                .map((record) -> record.key() + "=" + record.value())
                .reduce((a, b) -> a + ", " + b).orElse("");

        System.out.println("results={" + stringResult + "}");

        assertTrue(results.size() == 2);

        assertEquals(pageView1, pageViewSerde.deserializer().deserialize(null, getValue(results, "/home").getBytes()));
        assertEquals(pageView2, pageViewSerde.deserializer().deserialize(null, getValue(results, "/products").getBytes()));
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

    private static List<ConsumerRecord<String, String>> readOutput(String topic, int expectedKeys, long timeoutMillis) {

        consumer.subscribe(List.of(topic));

        List<ConsumerRecord<String, String>> results = new LinkedList<>();
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < timeoutMillis && (expectedKeys == 0 || results.size() < expectedKeys)) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                results.add(record);
            }
        }

        consumer.unsubscribe();

        return results;
    }

    private static String getValue(List<ConsumerRecord<String, String>> results, String key) {
        return results.stream()
                .filter(record -> record.key().equals(key))
                .reduce((first, second) -> second)
                .map(record -> record.value())
                .orElse(null);
    }
}
