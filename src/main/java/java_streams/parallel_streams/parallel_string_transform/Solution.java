package java_streams.parallel_streams.parallel_string_transform;


import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    // ["a", "b", "c"] -> ["A", "B", "C"]
    public List<String> parallelStringTransform(List<String> list) {
        return list.parallelStream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }
}
