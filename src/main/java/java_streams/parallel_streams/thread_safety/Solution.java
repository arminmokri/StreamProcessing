package java_streams.parallel_streams.thread_safety;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {

    // 1000 -> [anything some random numbers - from 0 to 999]
    public List<Integer> parallelThreadSafetyIssue(Integer num) {
        List<Integer> list = new ArrayList<>();
        // This is NOT thread-safe!
        IntStream
                .range(0, num)
                .parallel()
                .forEach(list::add);
        return list;
    }

    // 1000 -> [0, ..., 999]
    public List<Integer> parallelSafe(Integer num) {
        return IntStream
                .range(0, num)
                .parallel()
                .boxed()
                .collect(Collectors.toList());
    }
}
