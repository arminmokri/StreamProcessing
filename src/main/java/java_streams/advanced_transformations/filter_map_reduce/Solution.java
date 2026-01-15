package java_streams.advanced_transformations.filter_map_reduce;

import java.util.List;


public class Solution {

    // [1, 2, 3, 4, 5] -> 20
    public Integer filterMapReduce(List<Integer> list) {
        return list.stream()
                .filter(i -> i % 2 == 0)
                .map(i -> i * i)
                .reduce(Integer::sum)
                .orElse(0);
    }
}
