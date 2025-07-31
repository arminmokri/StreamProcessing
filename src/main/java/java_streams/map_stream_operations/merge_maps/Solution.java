package java_streams.map_stream_operations.merge_maps;


import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solution {

    public Map<String, Integer> mergeMaps(Map<String, Integer> map1, Map<String, Integer> map2) {
        return Stream.concat(
                map1.entrySet().stream(),
                map2.entrySet().stream()

        ).collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1 + v2
                )
        );
    }
}
