package java_streams.primitive_streams.summary_statistics;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.IntSummaryStatistics;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    record Statistic(Long Count, Long sum, Integer min, Integer max, Double avg) {
    }

    public Statistic summaryStatistics(List<Integer> list) {
        IntSummaryStatistics statistics = list.stream()
                .mapToInt(Integer::intValue)
                .summaryStatistics();

        return new Statistic(
                statistics.getCount(),
                statistics.getSum(),
                statistics.getMin(),
                statistics.getMax(),
                statistics.getAverage()
        );
    }
}


class SolutionTest {
    private static Solution solution;

    @BeforeAll
    public static void setUp() {
        solution = new Solution();
    }

    @Test
    public void testDefaultCase() {
        assertEquals(new Solution.Statistic(10L, 55L, 1, 10, 5.5d), solution.summaryStatistics(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
    }

    @Test
    public void testSingleElementList() {
        assertEquals(
                new Solution.Statistic(1L, 42L, 42, 42, 42.0),
                solution.summaryStatistics(List.of(42))
        );
    }

    @Test
    public void testNegativeNumbers() {
        assertEquals(
                new Solution.Statistic(3L, -6L, -3, -1, -2.0),
                solution.summaryStatistics(List.of(-1, -2, -3))
        );
    }

    @Test
    public void testMixedNumbers() {
        assertEquals(
                new Solution.Statistic(5L, 3L, -2, 4, 0.6),
                solution.summaryStatistics(List.of(-2, -1, 0, 2, 4))
        );
    }

    @Test
    public void testEmptyList() {
        assertEquals(
                new Solution.Statistic(0L, 0L, Integer.MAX_VALUE, Integer.MIN_VALUE, 0.0),
                solution.summaryStatistics(List.of())
        );
    }

}
