package kafka_streams.other.global_ktable_join;

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
import java.util.*;

public class Solution {

    record Customer(String id, String name, String phoneNumber, String address) {

        @Override
        public String toString() {
            return "Customer{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    ", address='" + address + '\'' +
                    '}';
        }
    }

    record Order(String id, String customerId, Integer amount) {

        @Override
        public String toString() {
            return "Order{" +
                    "id='" + id + '\'' +
                    ", customerId='" + customerId + '\'' +
                    ", amount=" + amount +
                    '}';
        }
    }

    record EnrichedOrder(String id, Integer amount, String customerId, String name, String phoneNumber,
                         String address) {

        EnrichedOrder(Order order, Customer customer) {
            this(
                    order.id(),
                    order.amount(),
                    customer.id(),
                    customer.name(),
                    customer.phoneNumber(),
                    customer.address()
            );
        }

        @Override
        public String toString() {
            return "EnrichedOrder{" +
                    "id='" + id + '\'' +
                    ", amount=" + amount +
                    ", customerId='" + customerId + '\'' +
                    ", name='" + name + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    ", address='" + address + '\'' +
                    '}';
        }
    }

    public static final String APPLICATION_NAME = "global_ktable_join";
    private static final String APPLICATION_ID = APPLICATION_NAME + "_" + UUID.randomUUID();
    private static final String CLIENT_ID = APPLICATION_NAME + "_client";
    public static final String BOOTSTRAP_SERVERS = "localhost:9092";

    private static Path STATE_DIR;

    private KafkaStreams streams;

    private Topology buildTopology(String inputTopicA, String inputTopicB, String outputTopic) {

        StreamsBuilder builder = new StreamsBuilder();

        // variable
        Consumed<String, Customer> consumedKTableCustomer = Consumed.with(
                Serdes.String(),
                getSerde(new TypeReference<Customer>() {
                })
        );
        Consumed<String, Order> consumedKStreamOrder = Consumed.with(
                Serdes.String(),
                getSerde(new TypeReference<Order>() {
                })
        );
        Produced<String, EnrichedOrder> produced = Produced.with(
                Serdes.String(),
                getSerde(new TypeReference<EnrichedOrder>() {
                })
        );
        KeyValueMapper<String, Order, String> keyValueMapper = (key, order) -> order.customerId();
        ValueJoiner<Order, Customer, EnrichedOrder> valueJoiner = (order, customer) -> new EnrichedOrder(order, customer);

        // input
        GlobalKTable<String, Customer> inputAKTableCustomer = builder
                .globalTable(inputTopicA, consumedKTableCustomer);

        KStream<String, Order> inputBKStreamOrder = builder
                .stream(inputTopicB, consumedKStreamOrder)
                .peek((key, value) -> {
                    if (Objects.nonNull(key) && Objects.nonNull(value)) {
                        System.out.println("input from topic(" + inputTopicB + ") -> key='" + key + "' value='" + value + "'");
                    }
                });

        // transform
        KStream<String, EnrichedOrder> joinedStream = inputBKStreamOrder
                .join(inputAKTableCustomer, keyValueMapper, valueJoiner);

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

