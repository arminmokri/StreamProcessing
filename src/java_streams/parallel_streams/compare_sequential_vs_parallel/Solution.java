package java_streams.parallel_streams.compare_sequential_vs_parallel;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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


class SolutionTest {
    private static Solution solution;

    @BeforeAll
    public static void setUp() {
        solution = new Solution();
    }


    @Test
    public void testParallelCase() {
        List<Integer> nums = IntStream.rangeClosed(1, 150_000_000).boxed().collect(Collectors.toList());
        long start = System.currentTimeMillis();
        assertEquals(11250000075000000L, solution.parallel(nums));
        long end = System.currentTimeMillis();
        System.out.println("testParallelCase: " + (end - start) + " ms");
    }

    @Test
    public void testSequentialCase() {
        List<Integer> nums = IntStream.rangeClosed(1, 150_000_000).boxed().collect(Collectors.toList());
        long start = System.currentTimeMillis();
        assertEquals(11250000075000000L, solution.sequential(nums));
        long end = System.currentTimeMillis();
        System.out.println("testSequentialCase: " + (end - start) + " ms");
    }


}
