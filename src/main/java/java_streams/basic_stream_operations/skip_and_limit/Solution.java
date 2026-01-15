package java_streams.basic_stream_operations.skip_and_limit;


import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    // [10, 20, 30, 40, 50], 1, 3 -> [20, 30, 40]
    public List<Integer> skipAndLimit(List<Integer> list, int skip, int limit) {
        return list.stream()
                .skip(skip)
                .limit(limit)
                .collect(Collectors.toList());
    }
}
