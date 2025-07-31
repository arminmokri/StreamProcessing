package java_streams.parallel_streams.compare_sequential_vs_parallel;


import java.util.List;

public class Solution {

    public Long parallel(List<Integer> list) {
        return list.parallelStream()
                .mapToLong(Integer::intValue)
                .sum();
    }

    public Long sequential(List<Integer> list) {
        return list.stream()
                .mapToLong(Integer::intValue)
                .sum();
    }
}
