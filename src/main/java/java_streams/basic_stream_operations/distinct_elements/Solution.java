package java_streams.basic_stream_operations.distinct_elements;


import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    public List<Integer> distinctElements(List<Integer> list) {
        return list.stream()
                .distinct()
                .collect(Collectors.toList());
    }
}
