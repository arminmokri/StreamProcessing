package kafka_streams.joins.kstream_ktable_join;

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
        if (Objects.nonNull(producer)) {
            producer.close();
        }
        if (Objects.nonNull(consumer)) {
            consumer.close();
        }

        solution.stopStream();
        Solution.deleteTopic(INPUT_TOPIC_A);
        Solution.deleteTopic(INPUT_TOPIC_B);
        Solution.deleteTopic(OUTPUT_TOPIC);
    }

    @Test
    public void testDefaultCase() {

        // variable

        // Customer(String id, String name, String phoneNumber, String address) - INPUT_TOPIC_A
        Serde<Solution.Customer> customerSerde = Solution.getSerde(
                new TypeReference<Solution.Customer>() {
                }
        );

        Solution.Customer customer1 = new Solution.Customer("1000", "Alice", "123456", "Toronto");
        Solution.Customer customer2 = new Solution.Customer("1001", "Bob", "123789", "Ottawa");

        // Order(String id, String customerId, Integer amount) - INPUT_TOPIC_B
        Serde<Solution.Order> orderSerde = Solution.getSerde(
                new TypeReference<Solution.Order>() {
                }
        );

        Solution.Order order1 = new Solution.Order("5000", "1000", 100);
        Solution.Order order2 = new Solution.Order("5001", "1001", 200);

        // EnrichedOrder(String id, Integer amount, String customerId, String name, String phoneNumber, String address) - OUTPUT_TOPIC
        Serde<Solution.EnrichedOrder> enrichedOrderSerde = Solution.getSerde(
                new TypeReference<Solution.EnrichedOrder>() {
                }
        );

        Solution.EnrichedOrder enrichedOrder1 = new Solution.EnrichedOrder("5000", 100, "1000", "Alice", "123456", "Toronto");
        Solution.EnrichedOrder enrichedOrder2 = new Solution.EnrichedOrder("5001", 200, "1001", "Bob", "123789", "Ottawa");

        // test

        sendInput(INPUT_TOPIC_A, "1000", new String(customerSerde.serializer().serialize(null, customer1)), null);
        sendInput(INPUT_TOPIC_A, "1001", new String(customerSerde.serializer().serialize(null, customer2)), null);

        sendInput(INPUT_TOPIC_B, "1000", new String(orderSerde.serializer().serialize(null, order1)), null);
        sendInput(INPUT_TOPIC_B, "1001", new String(orderSerde.serializer().serialize(null, order2)), null);

        List<ConsumerRecord<String, String>> results = readOutput(OUTPUT_TOPIC, 2, 5_000);

        String stringResult = results
                .stream()
                .map((record) -> record.key() + "=" + record.value())
                .reduce((a, b) -> a + ", " + b).orElse("");

        System.out.println("results={" + stringResult + "}");

        assertEquals(enrichedOrder1, enrichedOrderSerde.deserializer().deserialize(null, getValue(results, "1000").getBytes()));
        assertEquals(enrichedOrder2, enrichedOrderSerde.deserializer().deserialize(null, getValue(results, "1001").getBytes()));
    }

    private static void sendInput(String topic, String key, String value, Long timestamp) {

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
