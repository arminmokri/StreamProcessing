package java_streams.advanced_transformations.distinct_and_sorted;


import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    public List<Integer> distinctAndSorted(List<Integer> list) {
        return list.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
