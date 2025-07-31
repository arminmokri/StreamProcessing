package java_streams.reduction_and_aggregation.sum_integers;


import java.util.List;

public class Solution {

    public Integer sumIntegers(List<Integer> list) {
        return list.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
}
