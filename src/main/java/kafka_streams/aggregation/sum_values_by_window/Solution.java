package kafka_streams.aggregation.sum_values_by_window;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Solution {

    public static final String APPLICATION_NAME = "sum_values_by_window";
    private static final String APPLICATION_ID = APPLICATION_NAME + "_" + UUID.randomUUID();
    private static final String CLIENT_ID = APPLICATION_NAME + "_client";
    public static final String BOOTSTRAP_SERVERS = "localhost:9092";

    private static Path STATE_DIR;

    private KafkaStreams streams;

    private Topology buildTopology(String inputTopic, String outputTopic) {

        StreamsBuilder builder = new StreamsBuilder();

        Consumed<String, String> consumed = Consumed.with(Serdes.String(), Serdes.String());
        Produced<String, Long> produced = Produced.with(Serdes.String(), Serdes.Long());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        Grouped<String, Long> grouped = Grouped.with(Serdes.String(), Serdes.Long());
        TimeWindows windows = TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(5));

        // input
        KStream<String, String> inputKStream = builder
                .stream(inputTopic, consumed)
                .peek((key, value) -> {
                    if (Objects.nonNull(key) && Objects.nonNull(value)) {
                        System.out.println("input from topic(" + inputTopic + ") -> key='" + key + "' value='" + value + "'");
                    }
                });

        // transform
        KTable<Windowed<String>, Long> kTableSum = inputKStream
                .mapValues((readOnlyKey, value) -> {
                    try {
                        return Long.parseLong(value);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        return 0L;
                    }
                })
                .groupByKey(grouped)
                .windowedBy(windows)
                .reduce(Long::sum);

        KStream<String, Long> kStreamSumString = kTableSum
                .toStream()
                .map(((key, value) -> {
                    String windowStart = formatter.format(Instant.ofEpochMilli(key.window().start()));
                    String windowEnd = formatter.format(Instant.ofEpochMilli(key.window().end()));
                    String newKey = key.key() + "@" + windowStart + "-" + windowEnd;
                    return KeyValue.pair(newKey, value);
                }));

        // output
        kStreamSumString
                .peek((key, value) -> {
                    if (Objects.nonNull(key) && Objects.nonNull(value)) {
                        System.out.println("output to topic(" + outputTopic + ") -> key='" + key + "' value='" + value + "'");
                    }
                })
                .to(outputTopic, produced);

        return builder.build();
    }

    public void startStream(String inputTopic, String outputTopic) {

        Topology topology = buildTopology(inputTopic, outputTopic);

        try {
            STATE_DIR = Files.createTempDirectory(APPLICATION_ID).toAbsolutePath();
        } catch (IOException ioException) {
        }

        streams = new KafkaStreams(topology, getStreamsConfiguration());
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
        //props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        //props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

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

