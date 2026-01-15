package java_streams.basic_stream_operations.distinct_elements;


import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    // [1, 2, 2, 3, 3, 3] -> [1, 2, 3]
    public List<Integer> distinctElements(List<Integer> list) {
        return list.stream()
                .distinct()
                .collect(Collectors.toList());
    }
}
