package java_streams.parallel_streams.parallel_sum;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public Integer parallelSum(List<Integer> list) {
        return list.parallelStream()
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
        assertEquals(15, solution.parallelSum(List.of(1, 2, 3, 4, 5)));
    }

    @Test
    public void testEmptyList() {
        assertEquals(0, solution.parallelSum(List.of()));
    }

    @Test
    public void testSingleElement() {
        assertEquals(42, solution.parallelSum(List.of(42)));
    }

    @Test
    public void testWithNegativeNumbers() {
        assertEquals(0, solution.parallelSum(List.of(10, -5, -5)));
    }

    @Test
    public void testAllNegativeNumbers() {
        assertEquals(-15, solution.parallelSum(List.of(-5, -5, -5)));
    }

    @Test
    public void testLargeList() {
        List<Integer> list = java.util.stream.IntStream.rangeClosed(1, 1000)
                .boxed()
                .collect(Collectors.toList());
        assertEquals(500500, solution.parallelSum(list)); // 1+2+...+1000
    }

}
