package kafka_streams.real_world_use_cases.iot_sensor_aggregation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

public class Solution {

    record SensorEvent(String sensorId, Double temp) {
        @Override
        public String toString() {
            return "SensorEvent{" +
                    "sensorId='" + sensorId + '\'' +
                    ", temp=" + temp +
                    '}';
        }
    }

    static class SensorTemp {
        public String sensorId;
        public long count;
        public double sum;
        public double avg;

        public void addSensorEvent(SensorEvent sensorEvent) {
            if (Objects.isNull(sensorId)) {
                sensorId = sensorEvent.sensorId;
            }
            count++;
            sum = sum + sensorEvent.temp;
            avg = sum / count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SensorTemp that = (SensorTemp) o;

            if (count != that.count) return false;
            if (Double.compare(that.sum, sum) != 0) return false;
            if (Double.compare(that.avg, avg) != 0) return false;
            return Objects.equals(sensorId, that.sensorId);
        }

        @Override
        public String toString() {
            return "SensorTemp{" +
                    "sensorId='" + sensorId + '\'' +
                    ", count=" + count +
                    ", sum=" + sum +
                    ", avg=" + avg +
                    '}';
        }
    }

    public static final String APPLICATION_NAME = "iot_sensor_aggregation";
    private static final String APPLICATION_ID = APPLICATION_NAME + "_" + UUID.randomUUID();
    private static final String CLIENT_ID = APPLICATION_NAME + "_client";
    public static final String BOOTSTRAP_SERVERS = "localhost:9092";

    private Path STATE_DIR;

    private KafkaStreams streams;

    private Topology buildTopology(String inputTopic, String outputTopic) {

        StreamsBuilder builder = new StreamsBuilder();

        // variable
        Consumed<String, SensorEvent> consumedKStreamSensorEvent = Consumed.with(
                Serdes.String(),
                getSerde(new TypeReference<SensorEvent>() {
                })
        );
        Materialized<String, SensorTemp, KeyValueStore<Bytes, byte[]>> materializedStore =
                Materialized
                        .<String, SensorTemp, KeyValueStore<Bytes, byte[]>>as("aggregate_to_sensor_temp-store")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(
                                getSerde(new TypeReference<SensorTemp>() {
                                })
                        );
        Produced<String, SensorTemp> produced = Produced.with(
                Serdes.String(),
                getSerde(new TypeReference<SensorTemp>() {
                })
        );

        // input
        KStream<String, SensorEvent> inputKStream = builder
                .stream(inputTopic, consumedKStreamSensorEvent)
                .peek((key, value) ->
                        System.out.println(
                                "input from topic(" + inputTopic
                                        + ") -> key='" + (Objects.nonNull(key) ? key : "null")
                                        + "' value='" + (Objects.nonNull(value) ? value : "null") + "'"
                        )
                );

        // transform
        KTable<String, SensorTemp> inputKStreamAgg = inputKStream
                .groupByKey()
                .aggregate(
                        SensorTemp::new,
                        (key, event, aggregate) -> {
                            aggregate.addSensorEvent(event);
                            return aggregate;
                        },
                        materializedStore
                );

        // output
        inputKStreamAgg
                .toStream()
                .peek((key, value) ->
                        System.out.println(
                                "output to topic(" + outputTopic
                                        + ") -> key='" + (Objects.nonNull(key) ? key : "null")
                                        + "' value='" + (Objects.nonNull(value) ? value : "null") + "'"
                        )
                )
                .to(outputTopic, produced);

        return builder.build();
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

