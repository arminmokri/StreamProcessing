package java_streams.basic_stream_operations.filter_even_numbers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public List<Integer> filterEvenNumbers(List<Integer> list) {
        return list.stream()
                .filter(i -> i % 2 == 0)
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
        assertEquals(List.of(4, 10), solution.filterEvenNumbers(List.of(1, 4, 5, 10)));
    }

    @Test
    public void testAllEven() {
        assertEquals(List.of(2, 4, 6), solution.filterEvenNumbers(List.of(2, 4, 6)));
    }

    @Test
    public void testAllOdd() {
        assertEquals(List.of(), solution.filterEvenNumbers(List.of(1, 3, 5)));
    }

    @Test
    public void testEmptyList() {
        assertEquals(List.of(), solution.filterEvenNumbers(List.of()));
    }
}
