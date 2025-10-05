package kafka_streams.other.branching_streams;

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
    private static final String INPUT_TOPIC = Solution.APPLICATION_NAME + "_input";
    private static final String OUTPUT_TOPIC_A = Solution.APPLICATION_NAME + "_A" + "_output";
    private static final String OUTPUT_TOPIC_B = Solution.APPLICATION_NAME + "_B" + "_output";

    private static Solution solution;
    private static KafkaProducer<String, String> producer;
    private static KafkaConsumer<String, String> consumer;

    @BeforeAll
    public static void setup() throws Exception {
        solution = new Solution();

        // Clean up topics before starting
        Solution.deleteTopic(INPUT_TOPIC);
        Solution.deleteTopic(OUTPUT_TOPIC_A);
        Solution.deleteTopic(OUTPUT_TOPIC_B);
        Solution.createTopic(INPUT_TOPIC);
        Solution.createTopic(OUTPUT_TOPIC_A);
        Solution.createTopic(OUTPUT_TOPIC_B);

        // Start Kafka Streams
        solution.startStream(INPUT_TOPIC, OUTPUT_TOPIC_A, OUTPUT_TOPIC_B);

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
        Solution.deleteTopic(OUTPUT_TOPIC_A);
        Solution.deleteTopic(OUTPUT_TOPIC_B);
    }

    @Test
    public void testDefaultCase() {

        // variable

        // Customer(String id, String name, Integer purchaseAmount) - INPUT_TOPIC / OUTPUT_TOPIC_A / OUTPUT_TOPIC_B
        Serde<Solution.Customer> customerSerde = Solution.getSerde(
                new TypeReference<Solution.Customer>() {
                }
        );
        Solution.Customer customer1 = new Solution.Customer("1000", "Alice", 2500);
        Solution.Customer customer2 = new Solution.Customer("1001", "Bob", 450);
        Solution.Customer customer3 = new Solution.Customer("1002", "Michael", 1200);
        Solution.Customer customer4 = new Solution.Customer("1003", "Christian", 300);

        // test

        sendInput(INPUT_TOPIC, "1000", new String(customerSerde.serializer().serialize(null, customer1)), null);
        sendInput(INPUT_TOPIC, "1001", new String(customerSerde.serializer().serialize(null, customer2)), null);
        sendInput(INPUT_TOPIC, "1002", new String(customerSerde.serializer().serialize(null, customer3)), null);
        sendInput(INPUT_TOPIC, "1003", new String(customerSerde.serializer().serialize(null, customer4)), null);

        List<ConsumerRecord<String, String>> resultsA = readOutput(OUTPUT_TOPIC_A, 2, 5_000);

        String stringResultA = resultsA
                .stream()
                .map((record) -> record.key() + "=" + record.value())
                .reduce((a, b) -> a + ", " + b).orElse("");

        System.out.println("resultsA={" + stringResultA + "}");

        List<ConsumerRecord<String, String>> resultsB = readOutput(OUTPUT_TOPIC_B, 2, 5_000);

        String stringResultB = resultsB
                .stream()
                .map((record) -> record.key() + "=" + record.value())
                .reduce((a, b) -> a + ", " + b).orElse("");

        System.out.println("resultsB={" + stringResultB + "}");

        // resultsA
        assertEquals(customer2, customerSerde.deserializer().deserialize(null, getValue(resultsA, "1001").getBytes()));
        assertEquals(customer4, customerSerde.deserializer().deserialize(null, getValue(resultsA, "1003").getBytes()));

        // resultsB
        assertEquals(customer1, customerSerde.deserializer().deserialize(null, getValue(resultsB, "1000").getBytes()));
        assertEquals(customer3, customerSerde.deserializer().deserialize(null, getValue(resultsB, "1002").getBytes()));
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
