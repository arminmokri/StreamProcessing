package java_streams.map_stream_operations.collect_map_to_list;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    public List<Map.Entry> collectMapToList(Map<String, Integer> map) {
        return map
                .entrySet()
                .stream()
                .collect(Collectors.toList());
    }
}
