package java_streams.custom_collectors.collector_for_partitioned_lists;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    // [1, 2, 3, 4, 5, 6] -> {TRUE, [2, 4, 6], FALSE, [1, 3, 5]}
    public Map<Boolean, List<Integer>> collectorForPartitionedLists(List<Integer> list) {
        return list.stream()
                .collect(Collectors.partitioningBy(i -> i % 2 == 0));

    }
}
