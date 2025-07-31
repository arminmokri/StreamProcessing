package java_streams.parallel_streams.parallel_sum;


import java.util.List;

public class Solution {

    public Integer parallelSum(List<Integer> list) {
        return list.parallelStream()
                .mapToInt(Integer::intValue)
                .sum();
    }
}
