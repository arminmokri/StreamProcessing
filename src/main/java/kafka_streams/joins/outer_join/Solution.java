package kafka_streams.joins.outer_join;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
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
import java.time.Duration;
import java.util.*;

public class Solution {

    record Customer(String id, String name) {

        @Override
        public String toString() {
            return "Customer{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    record Order(String id, String customerId, List<String> items) {

        @Override
        public String toString() {
            return "Order{" +
                    "id='" + id + '\'' +
                    ", customerId='" + customerId + '\'' +
                    ", items=" + items +
                    '}';
        }
    }

    record EnrichOrder(String id, String customerId, List<String> items, String name) {

        EnrichOrder(Customer customer, Order order) {
            this(
                    order != null ? order.id : null,
                    order != null ? order.customerId : (customer != null ? customer.id : null),
                    order != null ? order.items : Collections.emptyList(),
                    customer != null ? customer.name : null
            );
        }

        @Override
        public String toString() {
            return "EnrichOrder{" +
                    "id='" + id + '\'' +
                    ", customerId='" + customerId + '\'' +
                    ", items=" + items +
                    ", name='" + name + '\'' +
                    '}';
        }
    }


    public static final String APPLICATION_NAME = "outer_join";
    private static final String APPLICATION_ID = APPLICATION_NAME + "_" + UUID.randomUUID();
    private static final String CLIENT_ID = APPLICATION_NAME + "_client";
    public static final String BOOTSTRAP_SERVERS = "localhost:9092";

    private static Path STATE_DIR;

    private KafkaStreams streams;

    private Topology buildTopology(String inputTopicA, String inputTopicB, String outputTopic) {

        StreamsBuilder builder = new StreamsBuilder();

        // variable
        Consumed<String, Customer> consumedKStreamCustomer = Consumed.with(
                Serdes.String(),
                getSerde(new TypeReference<Customer>() {
                })
        );
        Consumed<String, Order> consumedKStreamOrder = Consumed.with(
                Serdes.String(),
                getSerde(new TypeReference<Order>() {
                })
        );
        Produced<String, EnrichOrder> produced = Produced.with(
                Serdes.String(),
                getSerde(new TypeReference<EnrichOrder>() {
                })
        );
        ValueJoiner<Customer, Order, EnrichOrder> valueJoiner = (customer, order) -> new EnrichOrder(customer, order);
        JoinWindows joinWindows = JoinWindows.of(Duration.ofSeconds(3));
        StreamJoined<String, Customer, Order> streamJoined = StreamJoined.with(
                Serdes.String(),
                getSerde(new TypeReference<Customer>() {
                }),
                getSerde(new TypeReference<Order>() {
                })

        );

        // input
        KStream<String, Customer> inputAKStream = builder
                .stream(inputTopicA, consumedKStreamCustomer)
                .peek((key, value) -> {
                    if (Objects.nonNull(key) && Objects.nonNull(value)) {
                        System.out.println("input from topic(" + inputTopicA + ") -> key='" + key + "' value='" + value + "'");
                    }
                });

        KStream<String, Order> inputBKStream = builder
                .stream(inputTopicB, consumedKStreamOrder)
                .peek((key, value) -> {
                    if (Objects.nonNull(key) && Objects.nonNull(value)) {
                        System.out.println("input from topic(" + inputTopicB + ") -> key='" + key + "' value='" + value + "'");
                    }
                });

        // transform
        KStream<String, EnrichOrder> joinedStream = inputAKStream
                .outerJoin(inputBKStream, valueJoiner, joinWindows, streamJoined);

        // output
        joinedStream
                .peek((key, value) -> {
                    if (Objects.nonNull(key) && Objects.nonNull(value)) {
                        System.out.println("output to topic(" + outputTopic + ") -> key='" + key + "' value='" + value + "'");
                    }
                })
                .to(outputTopic, produced);


        return builder.build();
    }

    public void startStream(String inputTopicA, String inputTopicB, String outputTopic) {

        Topology topology = buildTopology(inputTopicA, inputTopicB, outputTopic);

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
//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // Records should be flushed every 100 ms. This is less than the default
        // in order to keep this example interactive.
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 200); // faster commit for testing

        // For illustrative purposes we disable record caches.
        props.put(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);

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

