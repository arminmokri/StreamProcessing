package kafka_streams.aggregation.aggregate_to_list;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        // List<String> - INPUT_TOPIC / OUTPUT_TOPIC
        Serde<List<String>> listOfStringSerde = kafka_streams.other.branching_streams.Solution.getSerde(
                new TypeReference<List<String>>() {
                }
        );

        List<String> list1 = List.of("1", "2");
        List<String> list2 = List.of("A", "B");
        List<String> list3 = List.of("3");
        List<String> list4 = List.of("C");
        List<String> list1Plus3 = List.of("1", "2", "3");
        List<String> list2Plus4 = List.of("A", "B", "C");

        // test

        sendInput(INPUT_TOPIC, "A", new String(listOfStringSerde.serializer().serialize(null, list1)), null);
        sendInput(INPUT_TOPIC, "B", new String(listOfStringSerde.serializer().serialize(null, list2)), null);
        sendInput(INPUT_TOPIC, "A", new String(listOfStringSerde.serializer().serialize(null, list3)), null);
        sendInput(INPUT_TOPIC, "B", new String(listOfStringSerde.serializer().serialize(null, list4)), null);

        List<ConsumerRecord<String, String>> results = readOutput(OUTPUT_TOPIC, 2, 5_000);

        String stringResult = results
                .stream()
                .map((record) -> record.key() + "=" + record.value())
                .reduce((a, b) -> a + ", " + b).orElse("");

        System.out.println("results={" + stringResult + "}");

        List<String> listA = null;
        List<String> listB = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            listA =
                    mapper.readValue(
                            new String(getValue(results, "A").getBytes(), StandardCharsets.UTF_8),
                            new TypeReference<List<String>>() {
                            }
                    );

            listB =
                    mapper.readValue(
                            new String(getValue(results, "B").getBytes(), StandardCharsets.UTF_8),
                            new TypeReference<List<String>>() {
                            }
                    );

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        assertEquals(list1Plus3, listA);
        assertEquals(list2Plus4, listB);
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
