package java_streams.custom_collectors.collector_for_frequency_map;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    public Map<String, Integer> collectorForFrequencyMap(List<String> list) {
        return list.stream()
                .collect(Collectors.groupingBy(
                                o -> o,
                                Collectors.collectingAndThen(
                                        Collectors.counting(),
                                        Long::intValue
                                )
                        )
                );

    }
}
