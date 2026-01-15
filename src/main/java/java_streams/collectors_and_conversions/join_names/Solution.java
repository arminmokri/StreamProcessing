package java_streams.collectors_and_conversions.join_names;


import java.util.List;
import java.util.stream.Collectors;

public class Solution {


    // ["Alice", "Bob"] -> "Alice, Bob"
    public String joinNames(List<String> list) {
        return list.stream()
                .collect(Collectors.joining(", "));
    }
}
