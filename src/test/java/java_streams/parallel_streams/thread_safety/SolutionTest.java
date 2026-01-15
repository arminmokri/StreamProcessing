package java_streams.parallel_streams.thread_safety;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SolutionTest {
    private static Solution solution;

    @BeforeAll
    public static void setUp() {
        solution = new Solution();
    }


    @Test
    public void testParallelThreadSafetyIssueCase() {
        List<Integer> list = solution.parallelThreadSafetyIssue(1000);
        assertEquals(1000, list.size(), 500);
    }

    @Test
    public void testParallelSafeCase() {
        List<Integer> list = solution.parallelSafe(1000);
        assertEquals(1000, list.size());
    }

}
