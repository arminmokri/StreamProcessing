package java_streams.advanced_transformations.filter_map_reduce;

import java.util.List;


public class Solution {

    public Integer filterMapReduce(List<Integer> list) {
        return list.stream()
                .filter(i -> i % 2 == 0)
                .map(i -> i * i)
                .reduce(Integer::sum)
                .orElse(0);
    }
}
