package java_streams.parallel_streams.parallel_filter_and_collect;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolutionTest {
    private static Solution solution;

    @BeforeAll
    public static void setUp() {
        solution = new Solution();
    }

    @Test
    public void testDefaultCase() {
        List<Integer> input = IntStream.rangeClosed(1, 100).boxed().collect(Collectors.toList()); // 1 to 100
        List<Integer> output = IntStream.iterate(2, i -> i + 2).limit(50).boxed().collect(Collectors.toList()); // even number 1 to 100
        assertEquals(output, solution.parallelFilterAndCollect(input));
    }

    @Test
    public void testEmptyList() {
        assertEquals(List.of(), solution.parallelFilterAndCollect(List.of()));
    }

    @Test
    public void testAllEvenNumbers() {
        List<Integer> input = List.of(2, 4, 6, 8);
        assertEquals(List.of(2, 4, 6, 8), solution.parallelFilterAndCollect(input));
    }

    @Test
    public void testAllOddNumbers() {
        List<Integer> input = List.of(1, 3, 5, 7);
        assertEquals(List.of(), solution.parallelFilterAndCollect(input));
    }

    @Test
    public void testNegativeNumbers() {
        List<Integer> input = List.of(-10, -9, -8, -7, -6);
        assertEquals(List.of(-10, -8, -6), solution.parallelFilterAndCollect(input));
    }

    @Test
    public void testMixedNegativeAndPositive() {
        List<Integer> input = List.of(-4, -3, 0, 1, 2, 3, 4);
        assertEquals(List.of(-4, 0, 2, 4), solution.parallelFilterAndCollect(input));
    }

    @Test
    public void testWithDuplicates() {
        List<Integer> input = List.of(2, 2, 3, 4, 4, 5);
        assertEquals(List.of(2, 2, 4, 4), solution.parallelFilterAndCollect(input));
    }

    @Test
    public void testSingleEvenElement() {
        List<Integer> input = List.of(6);
        assertEquals(List.of(6), solution.parallelFilterAndCollect(input));
    }

    @Test
    public void testSingleOddElement() {
        List<Integer> input = List.of(7);
        assertEquals(List.of(), solution.parallelFilterAndCollect(input));
    }

}
