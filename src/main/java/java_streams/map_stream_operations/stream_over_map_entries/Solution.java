package java_streams.map_stream_operations.stream_over_map_entries;


import java.util.Map;

public class Solution {

    // {"a", 1, "b", 2} ->
    // "a=1\n"
    // "b=2\n"
    public void streamOverMapEntries(Map<String, Integer> map) {
        map.entrySet()
                .stream()
                .forEach(entry -> System.out.println(entry.getKey() + "=" + entry.getValue()));
    }
}
