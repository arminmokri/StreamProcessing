package kafka_streams.real_world_use_cases.log_enrichment;

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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SolutionTest {
    private static final String INPUT_TOPIC = Solution.APPLICATION_NAME + "_input";
    private static final String OUTPUT_TOPIC = Solution.APPLICATION_NAME + "_output";

    private Solution solution;
    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;

    @Mock
    private Solution.GeoIpService geoIpService;

    @BeforeAll
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        solution = new Solution(geoIpService);

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

        // variable

        // Log(String ip, String message) - INPUT_TOPIC
        Serde<Solution.Log> logSerde = solution.getSerde(
                new TypeReference<Solution.Log>() {
                }
        );

        Solution.Log log1 = new Solution.Log("108.162.165.210", "login");
        Solution.Log log2 = new Solution.Log("37.156.13.178", "logout");

        // EnrichedLog(String ip, String message, String country) - OUTPUT_TOPIC
        Serde<Solution.EnrichedLog> enrichedLogSerde = solution.getSerde(
                new TypeReference<Solution.EnrichedLog>() {
                }
        );

        Solution.EnrichedLog enrichedLog1 = new Solution.EnrichedLog("108.162.165.210", "login", "CA");
        Solution.EnrichedLog enrichedLog2 = new Solution.EnrichedLog("37.156.13.178", "logout", "IR");

        // test
        when(geoIpService.getCountry("108.162.165.210"))
                .thenReturn(Optional.of("CA"));
        when(geoIpService.getCountry("37.156.13.178"))
                .thenReturn(Optional.of("IR"));

        sendInput(INPUT_TOPIC, "user1", new String(logSerde.serializer().serialize(null, log1)), null);
        sendInput(INPUT_TOPIC, "user2", new String(logSerde.serializer().serialize(null, log2)), null);

        List<ConsumerRecord<String, String>> results = readOutput(OUTPUT_TOPIC, 0, 5_000);

        String stringResult = results
                .stream()
                .map((record) -> record.key() + "=" + record.value())
                .reduce((a, b) -> a + ", " + b).orElse("");

        System.out.println("results={" + stringResult + "}");

        assertTrue(results.size() == 2);

        assertEquals(enrichedLog1, enrichedLogSerde.deserializer().deserialize(null, getValue(results, "user1").getBytes()));
        assertEquals(enrichedLog2, enrichedLogSerde.deserializer().deserialize(null, getValue(results, "user2").getBytes()));
    }

    @Test
    public void testApiFailsOnceThenSucceedsOnRetry() {

        // variable

        // Log(String ip, String message) - INPUT_TOPIC
        Serde<Solution.Log> logSerde = solution.getSerde(
                new TypeReference<Solution.Log>() {
                }
        );

        Solution.Log log1 = new Solution.Log("108.162.165.210", "login");

        // EnrichedLog(String ip, String message, String country) - OUTPUT_TOPIC
        Serde<Solution.EnrichedLog> enrichedLogSerde = solution.getSerde(
                new TypeReference<Solution.EnrichedLog>() {
                }
        );

        Solution.EnrichedLog enrichedLog1 = new Solution.EnrichedLog("108.162.165.210", "login", "CA");

        // test
        when(geoIpService.getCountry("108.162.165.210"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of("CA"));

        sendInput(INPUT_TOPIC, "user1", new String(logSerde.serializer().serialize(null, log1)), null);

        List<ConsumerRecord<String, String>> results = readOutput(OUTPUT_TOPIC, 0, 5_000);

        String stringResult = results
                .stream()
                .map((record) -> record.key() + "=" + record.value())
                .reduce((a, b) -> a + ", " + b).orElse("");

        System.out.println("results={" + stringResult + "}");

        assertTrue(results.size() == 1);

        assertEquals(enrichedLog1, enrichedLogSerde.deserializer().deserialize(null, getValue(results, "user1").getBytes()));
    }

    @Test
    public void testApiFails() {

        // variable

        // Log(String ip, String message) - INPUT_TOPIC
        Serde<Solution.Log> logSerde = solution.getSerde(
                new TypeReference<Solution.Log>() {
                }
        );

        Solution.Log log1 = new Solution.Log("108.162.165.210", "login");

        // test
        when(geoIpService.getCountry(anyString()))
                .thenReturn(Optional.empty());

        sendInput(INPUT_TOPIC, "user1", new String(logSerde.serializer().serialize(null, log1)), null);

        List<ConsumerRecord<String, String>> results = readOutput(OUTPUT_TOPIC, 0, 5_000);

        String stringResult = results
                .stream()
                .map((record) -> record.key() + "=" + record.value())
                .reduce((a, b) -> a + ", " + b).orElse("");

        System.out.println("results={" + stringResult + "}");

        assertTrue(results.size() == 0);
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

        //consumer.unsubscribe();

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
