package java_streams.reduction_and_aggregation.count_elements;


import java.util.List;

public class Solution {

    // [1, 2, 3, 4] -> 4
    public Long countElements(List<Integer> list) {
        return list.stream()
                .count();
    }
}
