package kafka_streams.joins.windowed_join;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
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
import org.junit.jupiter.api.TestInstance;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SolutionTest {
    private static final String INPUT_TOPIC_A = Solution.APPLICATION_NAME + "_A" + "_input";
    private static final String INPUT_TOPIC_B = Solution.APPLICATION_NAME + "_B" + "_input";
    private static final String OUTPUT_TOPIC = Solution.APPLICATION_NAME + "_output";

    private Solution solution;
    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;

    @BeforeAll
    public void setup() throws Exception {
        solution = new Solution();

        // Clean up topics before starting
        deleteTopic(INPUT_TOPIC_A);
        deleteTopic(INPUT_TOPIC_B);
        deleteTopic(OUTPUT_TOPIC);
        createTopic(INPUT_TOPIC_A);
        createTopic(INPUT_TOPIC_B);
        createTopic(OUTPUT_TOPIC);

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
    public void cleanup() {
        if (Objects.nonNull(producer)) {
            producer.close();
        }
        if (Objects.nonNull(consumer)) {
            consumer.close();
        }

        solution.stopStream();
        deleteTopic(INPUT_TOPIC_A);
        deleteTopic(INPUT_TOPIC_B);
        deleteTopic(OUTPUT_TOPIC);
    }

    @Test
    public void testDefaultCase() {

        // variable

        // Order(String id, List<String> items) - INPUT_TOPIC_A
        Serde<Solution.Order> orderSerde = solution.getSerde(
                new TypeReference<Solution.Order>() {
                }
        );

        Solution.Order order1 = new Solution.Order("1000", List.of("shoes", "bag"));
        Solution.Order order2 = new Solution.Order("1001", List.of("iPhone", "AirPods"));
        Solution.Order order3 = new Solution.Order("1002", List.of("Galaxy"));

        // Payment(String id, String orderId, Integer amount) - INPUT_TOPIC_B
        Serde<Solution.Payment> paymentSerde = solution.getSerde(
                new TypeReference<Solution.Payment>() {
                }
        );

        Solution.Payment payment1 = new Solution.Payment("2000", "1000", 100);
        Solution.Payment payment2 = new Solution.Payment("2001", "1001", 2000);
        Solution.Payment payment3 = new Solution.Payment("2001", "1002", 500);

        // EnrichedOrder(String id, List<String> items, String paymentId, Integer amount) - OUTPUT_TOPIC
        Serde<Solution.EnrichedOrder> enrichedOrderSerde = solution.getSerde(
                new TypeReference<Solution.EnrichedOrder>() {
                }
        );

        Solution.EnrichedOrder enrichedOrder1 = new Solution.EnrichedOrder("1000", List.of("shoes", "bag"), "2000", 100);
        Solution.EnrichedOrder enrichedOrder2 = new Solution.EnrichedOrder("1001", List.of("iPhone", "AirPods"), "2001", 2000);

        // test

        long baseTime = System.currentTimeMillis();

        // t=0s
        sendInput(INPUT_TOPIC_A, "1000", new String(orderSerde.serializer().serialize(null, order1)), baseTime);

        // t=3s
        sendInput(INPUT_TOPIC_B, "1000", new String(paymentSerde.serializer().serialize(null, payment1)), baseTime + 3000);

        // t=6s
        sendInput(INPUT_TOPIC_A, "1001", new String(orderSerde.serializer().serialize(null, order2)), baseTime + 6000);
        sendInput(INPUT_TOPIC_B, "1001", new String(paymentSerde.serializer().serialize(null, payment2)), baseTime + 6000);

        // t=7s
        sendInput(INPUT_TOPIC_A, "1002", new String(orderSerde.serializer().serialize(null, order3)), baseTime + 7000);

        // t=13s
        sendInput(INPUT_TOPIC_B, "1002", new String(paymentSerde.serializer().serialize(null, payment3)), baseTime + 14000);


        List<ConsumerRecord<String, String>> results = readOutput(OUTPUT_TOPIC, 2, 5_000);

        String stringResult = results
                .stream()
                .map((record) -> record.key() + "=" + record.value())
                .reduce((a, b) -> a + ", " + b).orElse("");

        System.out.println("results={" + stringResult + "}");


        assertEquals(enrichedOrder1, enrichedOrderSerde.deserializer().deserialize(null, getValue(results, "1000").getBytes()));
        assertEquals(enrichedOrder2, enrichedOrderSerde.deserializer().deserialize(null, getValue(results, "1001").getBytes()));
        assertNull(getValue(results, "1002"));

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

    private List<ConsumerRecord<String, String>> readOutput(String topic, int expectedKeys, long timeoutMillis) {

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

    private String getValue(List<ConsumerRecord<String, String>> results, String key) {
        return results.stream()
                .filter(record -> record.key().equals(key))
                .reduce((first, second) -> second)
                .map(record -> record.value())
                .orElse(null);
    }

    private void createTopic(String topic) {
        try (AdminClient admin = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, Solution.BOOTSTRAP_SERVERS))) {
            admin.createTopics(List.of(new NewTopic(topic, 1, (short) 1))).all().get();
        } catch (Exception exception) {

        }
    }

    private void deleteTopic(String topic) {
        try (AdminClient admin = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, Solution.BOOTSTRAP_SERVERS))) {
            admin.deleteTopics(List.of(topic)).all().get();
        } catch (Exception exception) {

        }
    }

}
