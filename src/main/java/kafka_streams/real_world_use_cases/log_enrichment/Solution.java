package kafka_streams.real_world_use_cases.log_enrichment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;

public class Solution {

    record Log(String ip, String message) {
        @Override
        public String toString() {
            return "Log{" +
                    "ip='" + ip + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    record EnrichedLog(String ip, String message, String country) {
        @Override
        public String toString() {
            return "EnrichedLog{" +
                    "ip='" + ip + '\'' +
                    ", message='" + message + '\'' +
                    ", country='" + country + '\'' +
                    '}';
        }
    }

    interface GeoIpService {
        Optional<String> getCountry(String ip);
    }

    static class LogEnrichmentProcessor extends AbstractProcessor<String, String> {

        private KeyValueStore<String, String> dlqLogStore;
        private Serde<Log> logSerde;
        private Serde<EnrichedLog> enrichedLogSerde;
        private final GeoIpService geoIpService;

        LogEnrichmentProcessor(GeoIpService geoIpService) {
            this.geoIpService = geoIpService;
            logSerde = getSerde(
                    new TypeReference<Log>() {
                    }
            );
            enrichedLogSerde = getSerde(
                    new TypeReference<EnrichedLog>() {
                    }
            );
        }

        @Override
        public void init(ProcessorContext context) {
            super.init(context);
            dlqLogStore = context.getStateStore("dlq-log-store");

            // retry every 2 seconds
            context.schedule(
                    Duration.ofSeconds(2),
                    PunctuationType.WALL_CLOCK_TIME,
                    timestamp -> retryDlqEvents(timestamp)
            );
        }

        @Override
        public void process(String key, String value) {

            System.out.println(
                    "input from topic"
                            + " -> key='" + (Objects.nonNull(key) ? key : "null")
                            + "' value='" + (Objects.nonNull(value) ? value : "null") + "'"
            );

            Log valueLog = logSerde.deserializer().deserialize(null, value.getBytes());
            Optional<String> country = geoIpService.getCountry(valueLog.ip);

            if (country.isPresent()) {
                EnrichedLog enrichedLog = new EnrichedLog(valueLog.ip, valueLog.message, country.get());
                System.out.println(
                        "output to topic"
                                + " -> key='" + (Objects.nonNull(key) ? key : "null")
                                + "' value='" + (Objects.nonNull(enrichedLog) ? enrichedLog : "null") + "'"
                );
                context().forward(key, new String(enrichedLogSerde.serializer().serialize(null, enrichedLog)));
            } else {
                System.out.println(
                        "(API call Failed) send to dlq-log-store"
                                + " -> key='" + (Objects.nonNull(key) ? key : "null")
                                + "' value='" + (Objects.nonNull(value) ? value : "null") + "'"
                );
                dlqLogStore.put(key, value);
            }
        }

        private void retryDlqEvents(Long timestamp) {
            try (KeyValueIterator<String, String> it = dlqLogStore.all()) {
                while (it.hasNext()) {
                    KeyValue<String, String> entry = it.next();
                    String key = entry.key;
                    Log valueLog = logSerde.deserializer().deserialize(null, entry.value.getBytes());
                    Optional<String> country = geoIpService.getCountry(valueLog.ip);

                    System.out.println(
                            "input from dlq-log-store"
                                    + " -> key='" + (Objects.nonNull(key) ? key : "null")
                                    + "' value='" + (Objects.nonNull(valueLog) ? valueLog : "null") + "'"
                    );

                    if (country.isPresent()) {
                        EnrichedLog enrichedLog = new EnrichedLog(valueLog.ip, valueLog.message, country.get());
                        System.out.println(
                                "output to topic (DLQ)"
                                        + " -> key='" + (Objects.nonNull(key) ? key : "null")
                                        + "' value='" + (Objects.nonNull(enrichedLog) ? enrichedLog : "null") + "'"
                        );
                        context().forward(entry.key, new String(enrichedLogSerde.serializer().serialize(null, enrichedLog)));
                    } else {
                        System.out.println(
                                "(API call Failed) delete from dlq-log-store"
                                        + " -> key='" + (Objects.nonNull(key) ? key : "null")
                                        + "' value='" + (Objects.nonNull(valueLog) ? valueLog : "null") + "'"
                        );
                    }
                    dlqLogStore.delete(entry.key);
                }
            }
        }
    }


    public static final String APPLICATION_NAME = "log_enrichment";
    private static final String APPLICATION_ID = APPLICATION_NAME + "_" + UUID.randomUUID();
    private static final String CLIENT_ID = APPLICATION_NAME + "_client";
    public static final String BOOTSTRAP_SERVERS = "localhost:9092";

    private static Path STATE_DIR;

    private KafkaStreams streams;

    private final GeoIpService geoIpService;

    public Solution(GeoIpService geoIpService) {
        this.geoIpService = geoIpService;
    }

    private Topology buildTopology(String inputTopic, String outputTopic) {
        Topology topology = new Topology();

        // variable
        StoreBuilder<KeyValueStore<String, String>> dlqLogStore =
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore("dlq-log-store"),
                        Serdes.String(),
                        Serdes.String()
                );

        // Source topic
        topology.addSource(
                "Source",
                inputTopic
        );

        // Add processor with state store
        topology.addProcessor(
                "LogEnrichmentProcessor",
                () -> new LogEnrichmentProcessor(geoIpService),
                "Source"
        );

        topology.addStateStore(
                dlqLogStore,
                "LogEnrichmentProcessor"
        );

        // Sink topic
        topology.addSink("Sink", outputTopic, "LogEnrichmentProcessor");

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

