package java_streams.reduction_and_aggregation.count_elements;


import java.util.List;

public class Solution {

    public Long countElements(List<Integer> list) {
        return list.stream()
                .count();
    }
}
