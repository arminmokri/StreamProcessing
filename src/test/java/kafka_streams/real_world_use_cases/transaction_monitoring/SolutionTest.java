package kafka_streams.real_world_use_cases.transaction_monitoring;

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
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SolutionTest {
    private static final String INPUT_TOPIC = Solution.APPLICATION_NAME + "_input";
    private static final String OUTPUT_TOPIC = Solution.APPLICATION_NAME + "_output";

    private Solution solution;
    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;

    @BeforeAll
    public void setup() throws Exception {
        solution = new Solution();

        // Clean up topics before starting
        deleteTopic(INPUT_TOPIC);
        deleteTopic(OUTPUT_TOPIC);
        createTopic(INPUT_TOPIC);
        createTopic(OUTPUT_TOPIC);

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
    public void cleanup() {
        if (Objects.nonNull(producer)) {
            producer.close();
        }
        if (Objects.nonNull(consumer)) {
            consumer.close();
        }

        solution.stopStream();
        deleteTopic(INPUT_TOPIC);
        deleteTopic(OUTPUT_TOPIC);
    }

    @Test
    public void testDefaultCase() {

        /*
            E1: user1:900   at 0m
            E2: user1:1200  at 2m
            E3: user2:300   at 3m
            E4: user3:300   at 6m

            |      Time → 0     1     2     3     4     5     6     7     8     9     10    11    12    13 |
            |     Frame → |-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|  |
            |    Event  → E1          E2    E3                                                             |
            | Windows 1 → [0---------------------------5) user1:E1 / user1:E1+E2 / user2:E2                |
            | Windows 2 →      [1----------------------------6) user3:E4                                   |
         */

        // variable

        // Purchase(String name, String item, Integer amount) - INPUT_TOPIC
        Serde<Solution.Transaction> transactionSerde = solution.getSerde(
                new TypeReference<Solution.Transaction>() {
                }
        );

        Solution.Transaction purchase1 = new Solution.Transaction("user1", 800d);
        Solution.Transaction purchase2 = new Solution.Transaction("user1", 900d);
        Solution.Transaction purchase3 = new Solution.Transaction("user2", 300d);
        Solution.Transaction purchase4 = new Solution.Transaction("user3", 700d);

        // FraudResult(String userId, Double totalAmount, FraudStatus fraudStatus) - OUTPUT_TOPIC
        Serde<Solution.FraudResult> fraudResultSerde = solution.getSerde(
                new TypeReference<Solution.FraudResult>() {
                }
        );

        Solution.FraudResult fraudResult1 = new Solution.FraudResult("user1", 1700d, Solution.FraudResult.FraudStatus.FRAUD);

        // test

        long baseTime = 0L;
        long eachMinToMilliSec = 60_000;

        // t=0m
        sendInput(INPUT_TOPIC, "user1", new String(transactionSerde.serializer().serialize(null, purchase1)), baseTime + (0 * eachMinToMilliSec));

        // t=2m
        sendInput(INPUT_TOPIC, "user1", new String(transactionSerde.serializer().serialize(null, purchase2)), baseTime + (2 * eachMinToMilliSec));

        // t=3m
        sendInput(INPUT_TOPIC, "user2", new String(transactionSerde.serializer().serialize(null, purchase3)), baseTime + (3 * eachMinToMilliSec));

        // t=6m
        sendInput(INPUT_TOPIC, "user3", new String(transactionSerde.serializer().serialize(null, purchase4)), baseTime + (6 * eachMinToMilliSec));


        List<ConsumerRecord<String, String>> results = readOutput(OUTPUT_TOPIC, 0, 5_000);

        String stringResult = results
                .stream()
                .map((record) -> record.key() + "=" + record.value())
                .reduce((a, b) -> a + ", " + b).orElse("");

        System.out.println("results={" + stringResult + "}");

        assertTrue(results.size() == 4);

        Map<String, Solution.FraudResult> frauds = new HashMap<>();
        results.forEach((record) -> {
            String key = record.key();
            String userId = key.split("@")[0];
            Solution.FraudResult fraudResult =
                    fraudResultSerde.deserializer().deserialize(null, getValue(results, key).getBytes());
            frauds.put(userId, fraudResult);
        });

        assertEquals(fraudResult1, frauds.get("user1"));
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
