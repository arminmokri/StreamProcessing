package kafka_streams.other.branching_streams;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

public class Solution {

    record Customer(String id, String name, Integer purchaseAmount) {

        @Override
        public String toString() {
            return "Customer{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", purchaseAmount=" + purchaseAmount +
                    '}';
        }
    }

    public static final String APPLICATION_NAME = "branching_streams";
    private static final String APPLICATION_ID = APPLICATION_NAME + "_" + UUID.randomUUID();
    private static final String CLIENT_ID = APPLICATION_NAME + "_client";
    public static final String BOOTSTRAP_SERVERS = "localhost:9092";

    private Path STATE_DIR;

    private KafkaStreams streams;

    private Topology buildTopology(String inputTopic, String outputTopicA, String outputTopicB) {

        StreamsBuilder builder = new StreamsBuilder();

        // variable
        Consumed<String, Customer> consumed = Consumed.with(
                Serdes.String(),
                getSerde(new TypeReference<Customer>() {
                })
        );
        Produced<String, Customer> produced = Produced.with(
                Serdes.String(),
                getSerde(new TypeReference<Customer>() {
                })
        );
        Predicate<String, Customer> isLessThan1000 = (key, value) -> value.purchaseAmount() < 1000;
        Branched<String, Customer> lessThan1000 = Branched.withConsumer(ks -> ks.to(outputTopicA, produced));
        Predicate<String, Customer> isEqualOrMoreThan1000 = (key, value) -> value.purchaseAmount() >= 1000;
        Branched<String, Customer> equalOrMoreThan1000 = Branched.withConsumer(ks -> ks.to(outputTopicB, produced));

        // input
        KStream<String, Customer> inputKStream = builder
                .stream(inputTopic, consumed)
                .peek((key, value) ->
                        System.out.println(
                                "input from topic(" + inputTopic
                                        + ") -> key='" + (Objects.nonNull(key) ? key : "null")
                                        + "' value='" + (Objects.nonNull(value) ? value : "null") + "'"
                        )
                );

        // transform

        // output
        inputKStream
                .split()
                .branch(isLessThan1000, lessThan1000)
                .branch(isEqualOrMoreThan1000, equalOrMoreThan1000);

        return builder.build();
    }

    public void startStream(String inputTopic, String outputTopicA, String outputTopicB) {

        Topology topology = buildTopology(inputTopic, outputTopicA, outputTopicB);

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

    private Properties getStreamsConfiguration() {

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

    public <T> Serde<T> getSerde(TypeReference<T> typeRef) {
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
}

