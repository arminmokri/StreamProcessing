package kafka_streams.basics.filter_and_map_kafka_records;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Solution {

    record UserEvent(String name, int age) {
        @Override
        public String toString() {
            return "UserEvent{name='" + name + "', age=" + age + '}';
        }
    }

    private static final String APPLICATION_ID = "word_count" + "_" + UUID.randomUUID();
    private static final String CLIENT_ID = "word_count" + "_client";
    public static final String BOOTSTRAP_SERVERS = "localhost:9092";

    private static Path STATE_DIR;

    private KafkaStreams streams;

    private StreamsBuilder builder;

    public void buildFilterAndMapKafkaRecordsTopology(String inputTopic, String outputTopic) {

        ObjectMapper mapper = new ObjectMapper();

        builder = new StreamsBuilder();

        KStream<String, String> stream = builder.stream(inputTopic);

        KStream<String, String> userEventKTable = stream
                .mapValues(str -> {
                    try {
                        return mapper.readValue(str, UserEvent.class);
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                })
                .filter((key, userEvent) -> Objects.nonNull(userEvent))
                .filter((key, userEvent) -> userEvent.age >= 18)
                .peek((key, userEvent) -> System.out.println("key='" + key + "' value='" + userEvent.toString() + "'"))
                .map((key, userEvent) -> KeyValue.pair("name", userEvent.name));

        userEventKTable.to(outputTopic);
    }

    public void startStream(String inputTopic, String outputTopic) {

        buildFilterAndMapKafkaRecordsTopology(inputTopic, outputTopic);

        try {
            STATE_DIR = Files.createTempDirectory(APPLICATION_ID).toAbsolutePath();
        } catch (IOException ioException) {
        }

        streams = new KafkaStreams(builder.build(), getStreamsConfiguration());
        streams.start();

        // Wait briefly to ensure the topology is ready
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopStream() {
        if (streams != null) {
            streams.close();
        }

        if (Objects.nonNull(STATE_DIR)) {
            try {
                Files.walk(STATE_DIR)
                        .sorted(Comparator.reverseOrder())
                        .forEach(file -> {
                                    try {
                                        Files.delete(file);
                                    } catch (IOException ioException) {
                                    }
                                }
                        );
            } catch (IOException ioException) {

            }

        }
    }

    private static Properties getStreamsConfiguration() {

        Properties props = new Properties();

        // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
        // against which the application is run.
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        props.put(StreamsConfig.CLIENT_ID_CONFIG, CLIENT_ID);

        // Where to find Kafka broker(s).
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // Specify default (de)serializers for record keys and for record values.
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // Records should be flushed every 100 ms. This is less than the default
        // in order to keep this example interactive.
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 200); // faster commit for testing

        // For illustrative purposes we disable record caches.
        //props.put(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);

        // Use a temporary directory for storing state, which will be automatically removed after the test.
        props.put(StreamsConfig.STATE_DIR_CONFIG, STATE_DIR.toString());

        return props;
    }

    public static void createTopic(String topic) {
        try (AdminClient admin = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS))) {
            admin.createTopics(List.of(new NewTopic(topic, 1, (short) 1))).all().get();
        } catch (Exception exception) {

        }
    }

    public static void deleteTopic(String topic) {
        try (AdminClient admin = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS))) {
            admin.deleteTopics(List.of(topic)).all().get();
        } catch (Exception exception) {

        }
    }
}

