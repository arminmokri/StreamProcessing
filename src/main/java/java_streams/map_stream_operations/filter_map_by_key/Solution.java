package java_streams.map_stream_operations.filter_map_by_key;


import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    public Map<String, Integer> filterMapByKey(Map<String, Integer> map, String startsWith) {
        return map
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(startsWith))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
