package java_streams.parallel_streams.thread_safety;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SolutionTest {
    private static Solution solution;

    @BeforeAll
    public static void setUp() {
        solution = new Solution();
    }


    @Test
    public void testParallelThreadSafetyIssueCase() {
        assertEquals(1000, solution.parallelThreadSafetyIssue(1000).size(), 500);
    }

    @Test
    public void testParallelSafeCase() {
        assertEquals(1000, solution.parallelSafe(1000).size());
    }

}
