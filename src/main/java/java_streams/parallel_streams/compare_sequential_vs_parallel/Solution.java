package java_streams.parallel_streams.compare_sequential_vs_parallel;


import java.util.List;

public class Solution {

    // [1, ..., 150_000_000] -> 11_250_000_075_000_000L
    public Long parallel(List<Integer> list) {
        return list.parallelStream()
                .mapToLong(Integer::intValue)
                .sum();
    }

    // [1, ..., 150_000_000] -> 11_250_000_075_000_000L
    public Long sequential(List<Integer> list) {
        return list.stream()
                .mapToLong(Integer::intValue)
                .sum();
    }
}
