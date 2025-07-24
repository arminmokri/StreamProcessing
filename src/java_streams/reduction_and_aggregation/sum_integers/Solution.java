package java_streams.reduction_and_aggregation.sum_integers;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public Integer sumIntegers(List<Integer> list) {
        return list.stream()
                .mapToInt(Integer::intValue)
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
    public void testDefaultCase() {
        assertEquals(6, solution.sumIntegers(List.of(1, 2, 3)));

    }

    @Test
    public void testEmptyList() {
        assertEquals(0, solution.sumIntegers(List.of()));
    }

    @Test
    public void testAllNegativeNumbers() {
        assertEquals(-15, solution.sumIntegers(List.of(-5, -3, -7)));
    }

    @Test
    public void testMixedPositiveAndNegativeNumbers() {
        assertEquals(0, solution.sumIntegers(List.of(10, -10, 5, -5)));
    }

    @Test
    public void testWithZeros() {
        assertEquals(3, solution.sumIntegers(List.of(0, 0, 3, 0)));
    }

    @Test
    public void testLargeNumbers() {
        assertEquals(2_000_000_000, solution.sumIntegers(List.of(1_000_000_000, 1_000_000_000)));
    }

}
