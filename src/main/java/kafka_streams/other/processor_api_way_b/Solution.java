package kafka_streams.other.processor_api_way_b;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Solution {


    static class SuspiciousLoginProcessor implements Processor<String, String, String, String> {

        private ProcessorContext<String, String> context;
        private KeyValueStore<String, Long> stateStore;

        @Override
        public void init(ProcessorContext<String, String> context) {
            this.context = context;
            this.stateStore = context.getStateStore("login-store");
        }

        @Override
        public void process(Record<String, String> record) {

            System.out.println(
                    "input from topic"
                            + " -> key='" + (Objects.nonNull(record) && Objects.nonNull(record.key()) ? record.key() : "null")
                            + "' value='" + (Objects.nonNull(record) && Objects.nonNull(record.value()) ? record.value() : "null") + "'"
            );

            long now = record.timestamp();
            Long lastLogin = stateStore.get(record.key());

            if (lastLogin != null && (now - lastLogin) <= 5000) {

                String key = record.key();
                String value = "Suspicious: " + key + " logged in twice within 5s";

                System.out.println(
                        "output to topic"
                                + " -> key='" + (Objects.nonNull(key) ? key : "null")
                                + "' value='" + (Objects.nonNull(value) ? value : "null") + "'"
                );

                context.forward(new Record<>(
                        key,
                        value,
                        record.timestamp()
                ));
            }
            stateStore.put(record.key(), now);
        }

        @Override
        public void close() {
        }
    }


    public static final String APPLICATION_NAME = "processor_api_way_b";
    private static final String APPLICATION_ID = APPLICATION_NAME + "_" + UUID.randomUUID();
    private static final String CLIENT_ID = APPLICATION_NAME + "_client";
    public static final String BOOTSTRAP_SERVERS = "localhost:9092";

    private static Path STATE_DIR;

    private KafkaStreams streams;

    private Topology buildTopology(String inputTopic, String outputTopic) {
        Topology topology = new Topology();

        // Source topic
        topology.addSource(
                "Source",
                inputTopic
        );

        // Add processor with state store
        topology.addProcessor(
                "SuspiciousProcessor",
                (ProcessorSupplier<String, String, String, String>) SuspiciousLoginProcessor::new,
                "Source"
        );

        topology.addStateStore(
                Stores.keyValueStoreBuilder(
                        Stores.inMemoryKeyValueStore("login-store"),
                        Serdes.String(),
                        Serdes.Long()
                ),
                "SuspiciousProcessor"
        );

        // Sink topic
        topology.addSink("Sink", outputTopic, "SuspiciousProcessor");

        return topology;

    }

    public void startStream(String inputTopic, String outputTopic) {

        Topology topology = buildTopology(inputTopic, outputTopic);

        System.out.println("Topology of " + APPLICATION_NAME);
        System.out.println(topology.describe());

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
        if (Objects.nonNull(streams)) {
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

    public static <T> Serde<T> getSerde(TypeReference<T> typeRef) {
        ObjectMapper mapper = new ObjectMapper();
        return Serdes.serdeFrom(
                (topic, data) -> {
                    try {
                        return mapper.writeValueAsString(data).getBytes(StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        return null;
                    }
                },
                (topic, data) -> {
                    try {
                        return mapper.readValue(new String(data, StandardCharsets.UTF_8), typeRef);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        return null;
                    }
                }
        );
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

