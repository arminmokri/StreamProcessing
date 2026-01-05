package kafka_streams.windowing.sliding_windows;

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
import org.apache.kafka.common.serialization.LongDeserializer;
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
    private KafkaConsumer<String, Long> consumer;

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
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
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
            E1: user1:/home    at 0s
            E2: user2:/about   at 1s
            E3: user1:/about   at 2s
            E4: user2:/home    at 3s
            E5: user1:/contact at 7s

            |      Time → 0     1     2     3     4     5     6     7     8     9     10    11    12    13 |
            |     Frame → |-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|  |
            |    Event  → E1    E2    E3    E4                      E5                                     |
            | Windows 1 → [0---------------------------5) user1:E1 / user1:E1+E3 / user2:E2+E4             |
            | Windows 2 →      [1----------------------------6) user2:E2                                   |
            | Windows 3 →            [2----------------------------7) user1:E3 / user1:E3+E5               |
         */

        long baseTime = 0L;
        long eachSecToMilliSec = 1_000;

        // t=0s
        sendInput(INPUT_TOPIC, "user1", "/home", baseTime + (0 * eachSecToMilliSec));

        // t=1s
        sendInput(INPUT_TOPIC, "user2", "/about", baseTime + (1 * eachSecToMilliSec));

        // t=2s
        sendInput(INPUT_TOPIC, "user1", "/about", baseTime + (2 * eachSecToMilliSec));

        // t=3s
        sendInput(INPUT_TOPIC, "user2", "/home", baseTime + (3 * eachSecToMilliSec));

        // t=7s
        sendInput(INPUT_TOPIC, "user1", "/contact", baseTime + (7 * eachSecToMilliSec));

        List<ConsumerRecord<String, Long>> results = readOutput(OUTPUT_TOPIC, 0, 5_000);

        String stringResult = results
                .stream()
                .map((record) -> record.key() + "=" + record.value())
                .reduce((a, b) -> a + ", " + b).orElse("");

        System.out.println("results={" + stringResult + "}");

        assertTrue(results.size() == 6);

        Map<String, Long> totals = new HashMap<>();
        results.forEach((record) -> {
            String userId = record.key().split("@")[0];
            totals.merge(userId, record.value(), Long::sum);
        });

        assertEquals(6L, totals.get("user1"));
        assertEquals(3L, totals.get("user2"));
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

    private List<ConsumerRecord<String, Long>> readOutput(String topic, int expectedKeys, long timeoutMillis) {

        consumer.subscribe(List.of(topic));

        List<ConsumerRecord<String, Long>> results = new LinkedList<>();
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < timeoutMillis && (expectedKeys == 0 || results.size() < expectedKeys)) {
            ConsumerRecords<String, Long> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, Long> record : records) {
                results.add(record);
            }
        }

        consumer.unsubscribe();

        return results;
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
