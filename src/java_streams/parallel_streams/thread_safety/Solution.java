package java_streams.parallel_streams.thread_safety;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class Solution {

    public List<Integer> parallelThreadSafetyIssue(Integer num) {
        List<Integer> list = new ArrayList<>();
        // This is NOT thread-safe!
        IntStream
                .range(0, num)
                .parallel()
                .forEach(list::add);
        return list;
    }

    public List<Integer> parallelSafe(Integer num) {
        return IntStream
                .range(0, num)
                .parallel()
                .boxed()
                .collect(Collectors.toList());
    }
}


class SolutionTest {
    private static Solution solution;

    @BeforeAll
    public static void setUp() {
        solution = new Solution();
    }


    @Test
    public void testParallelThreadSafetyIssueCase() {
        assertEquals(1000, solution.parallelThreadSafetyIssue(1000).size());
    }

    @Test
    public void testParallelSafeCase() {
        assertEquals(1000, solution.parallelSafe(1000).size());
    }

}
