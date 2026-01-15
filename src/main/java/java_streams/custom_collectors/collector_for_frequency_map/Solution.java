package java_streams.custom_collectors.collector_for_frequency_map;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    // ["apple", "banana", "apple", "orange", "banana", "apple"] ->
    // {"apple", 3, "banana", 2, "orange", 1}
    public Map<String, Integer> collectorForFrequencyMap(List<String> list) {
        return list.stream()
                .collect(
                        Collectors.groupingBy(
                                o -> o,
                                Collectors.collectingAndThen(
                                        Collectors.counting(),
                                        Long::intValue
                                )
                        )
                );

    }
}
