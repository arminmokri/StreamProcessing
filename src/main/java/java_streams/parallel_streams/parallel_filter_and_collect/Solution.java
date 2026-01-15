package java_streams.parallel_streams.parallel_filter_and_collect;


import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    // [1, 2, ..., 100] -> [2, 4, ..., 100]
    public List<Integer> parallelFilterAndCollect(List<Integer> list) {
        return list
                .parallelStream()
                .filter(i -> i % 2 == 0)
                .collect(Collectors.toList());
    }
}
