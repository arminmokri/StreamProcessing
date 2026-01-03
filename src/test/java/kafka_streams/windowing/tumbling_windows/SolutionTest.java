package kafka_streams.windowing.tumbling_windows;

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

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SolutionTest {
    private static final String INPUT_TOPIC = Solution.APPLICATION_NAME + "_input";
    private static final String OUTPUT_TOPIC = Solution.APPLICATION_NAME + "_output";

    private static Solution solution;
    private static KafkaProducer<String, String> producer;
    private static KafkaConsumer<String, Long> consumer;

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
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
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

        /*

            E1: user1:/home    at 0s
            E2: user1:/about   at 2s
            E3: user2:/home    at 3s
            E4: user1:/contact at 7s

            |      Time → 0     1     2     3     4     5     6     7     8     9     10    11    12    13 |
            |     Frame → |-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|  |
            |     Event → E1          E2    E3                      E4                                     |
            | Windows 1 → [0---------------------------5) user1:E1+E2 / user2:E3                           |
            | Windows 2 →                               [5--------------------------10) user1:E4           |

         */

        long baseTime = 0;

        // t=0s
        sendInput(INPUT_TOPIC, "user1", "/home", baseTime + 0);

        // t=2s
        sendInput(INPUT_TOPIC, "user1", "/about", baseTime + 2000);

        // t=3s
        sendInput(INPUT_TOPIC, "user2", "/home", baseTime + 3000);

        // t=7s
        sendInput(INPUT_TOPIC, "user1", "/contact", baseTime + 7000);

        List<ConsumerRecord<String, Long>> results = readOutput(OUTPUT_TOPIC, 3, 5_000);

        String stringResult = results
                .stream()
                .map((record) -> record.key() + "=" + record.value())
                .reduce((a, b) -> a + ", " + b).orElse("");

        System.out.println("results={" + stringResult + "}");

        assertTrue(results.size() == 3);

        Map<String, Long> totals = new HashMap<>();
        results.forEach((record) -> {
            String userId = record.key().split("@")[0];
            totals.merge(userId, record.value(), Long::sum);
        });

        assertEquals(3L, totals.get("user1"));
        assertEquals(1L, totals.get("user2"));
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

    private static List<ConsumerRecord<String, Long>> readOutput(String topic, int expectedKeys, long timeoutMillis) {

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
}
