package java_streams.custom_collectors.collector_for_partitioned_lists;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    public Map<Boolean, List<Integer>> collectorForPartitionedLists(List<Integer> list) {
        return list.stream()
                .collect(Collectors.partitioningBy(i -> i % 2 == 0));

    }
}
