package java_streams.advanced_transformations.sliding_window_simulation;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public List<List<Integer>> slidingWindowSimulation(List<Integer> list, Integer n) {
        return IntStream
                .range(0, list.size() - n + 1)
                .mapToObj(i -> list.subList(i, i + n))
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
    public void testDefaultCase() {
        assertEquals(
                List.of(
                        List.of(1, 2, 3),
                        List.of(2, 3, 4),
                        List.of(3, 4, 5)

                ),
                solution.slidingWindowSimulation(List.of(1, 2, 3, 4, 5), 3));
    }

    @Test
    public void testWindowSizeOne() {
        assertEquals(
                List.of(
                        List.of(1),
                        List.of(2),
                        List.of(3)
                ),
                solution.slidingWindowSimulation(List.of(1, 2, 3), 1));
    }

    @Test
    public void testWindowSizeEqualListSize() {
        assertEquals(
                List.of(List.of(1, 2, 3)),
                solution.slidingWindowSimulation(List.of(1, 2, 3), 3));
    }

    @Test
    public void testWindowSizeGreaterThanListSize() {
        assertEquals(
                List.of(),
                solution.slidingWindowSimulation(List.of(1, 2, 3), 5));
    }

    @Test
    public void testEmptyList() {
        assertEquals(
                List.of(),
                solution.slidingWindowSimulation(List.of(), 2));
    }

    @Test
    public void testWithDuplicates() {
        assertEquals(
                List.of(
                        List.of(2, 2),
                        List.of(2, 2),
                        List.of(2, 2)
                ),
                solution.slidingWindowSimulation(List.of(2, 2, 2, 2), 2));
    }

    @Test
    public void testWithNegativeNumbers() {
        assertEquals(
                List.of(
                        List.of(-3, -2),
                        List.of(-2, -1),
                        List.of(-1, 0),
                        List.of(0, 1)
                ),
                solution.slidingWindowSimulation(List.of(-3, -2, -1, 0, 1), 2));
    }

}
