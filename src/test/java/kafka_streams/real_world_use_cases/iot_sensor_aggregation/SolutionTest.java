package kafka_streams.real_world_use_cases.iot_sensor_aggregation;

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
import org.junit.jupiter.api.TestInstance;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

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
        solution.deleteTopic(INPUT_TOPIC);
        solution.deleteTopic(OUTPUT_TOPIC);
        solution.createTopic(INPUT_TOPIC);
        solution.createTopic(OUTPUT_TOPIC);

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
        solution.deleteTopic(INPUT_TOPIC);
        solution.deleteTopic(OUTPUT_TOPIC);
    }

    @Test
    public void testDefaultCase() {

        // variable

        // SensorEvent(String sensorId, Double temp) - INPUT_TOPIC
        Serde<Solution.SensorEvent> sensorEventSerde = solution.getSerde(
                new TypeReference<Solution.SensorEvent>() {
                }
        );

        Solution.SensorEvent sensorEvent1 = new Solution.SensorEvent("S1", 20d);
        Solution.SensorEvent sensorEvent2 = new Solution.SensorEvent("S1", 22d);
        Solution.SensorEvent sensorEvent3 = new Solution.SensorEvent("S2", 30d);

        // PageView(String page, Long count) - OUTPUT_TOPIC
        Serde<Solution.SensorTemp> sensorTempSerde = solution.getSerde(
                new TypeReference<Solution.SensorTemp>() {
                }
        );

        Solution.SensorTemp sensorTemp1 = new Solution.SensorTemp();
        sensorTemp1.addSensorEvent(sensorEvent1);
        sensorTemp1.addSensorEvent(sensorEvent2);
        Solution.SensorTemp sensorTemp2 = new Solution.SensorTemp();
        sensorTemp2.addSensorEvent(sensorEvent3);

        // test

        sendInput(INPUT_TOPIC, "S1", new String(sensorEventSerde.serializer().serialize(null, sensorEvent1)), null);
        sendInput(INPUT_TOPIC, "S1", new String(sensorEventSerde.serializer().serialize(null, sensorEvent2)), null);
        sendInput(INPUT_TOPIC, "S2", new String(sensorEventSerde.serializer().serialize(null, sensorEvent3)), null);

        List<ConsumerRecord<String, String>> results = readOutput(OUTPUT_TOPIC, 0, 5_000);

        String stringResult = results
                .stream()
                .map((record) -> record.key() + "=" + record.value())
                .reduce((a, b) -> a + ", " + b).orElse("");

        System.out.println("results={" + stringResult + "}");

        assertTrue(results.size() == 2);

        assertEquals(sensorTemp1, sensorTempSerde.deserializer().deserialize(null, getValue(results, "S1").getBytes()));
        assertEquals(sensorTemp2, sensorTempSerde.deserializer().deserialize(null, getValue(results, "S2").getBytes()));
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
}
