package java_streams.map_stream_operations.sort_map_by_value;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    // {"x", 30, "y", 10, "z", 20} ->
    // {"y", 10, "z", 20, "x", 30}
    public Map<String, Integer> sortMapByValue(Map<String, Integer> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new // maintain sorted order
                ));
    }
}
