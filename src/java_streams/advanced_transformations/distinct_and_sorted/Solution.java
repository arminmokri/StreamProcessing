package java_streams.advanced_transformations.distinct_and_sorted;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public List<Integer> distinctAndSorted(List<Integer> list) {
        return list.stream()
                .distinct()
                .sorted()
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
        assertEquals(List.of(1, 2, 3, 5), solution.distinctAndSorted(List.of(5, 3, 1, 2, 3, 5)));
    }

    @Test
    public void testEmptyList() {
        assertEquals(List.of(), solution.distinctAndSorted(List.of()));
    }

    @Test
    public void testAllDuplicates() {
        assertEquals(List.of(7), solution.distinctAndSorted(List.of(7, 7, 7, 7)));
    }

    @Test
    public void testAlreadySorted() {
        assertEquals(List.of(1, 2, 3, 4), solution.distinctAndSorted(List.of(1, 2, 3, 4)));
    }

    @Test
    public void testReverseSorted() {
        assertEquals(List.of(1, 2, 3, 4), solution.distinctAndSorted(List.of(4, 3, 2, 1)));
    }

    @Test
    public void testWithNegativeNumbers() {
        assertEquals(List.of(-5, -2, 0, 3), solution.distinctAndSorted(List.of(3, -2, -5, 0, -2, 3)));
    }

    @Test
    public void testSingleElement() {
        assertEquals(List.of(10), solution.distinctAndSorted(List.of(10)));
    }

    @Test
    public void testMixedDuplicates() {
        assertEquals(List.of(2, 4, 5, 6, 9), solution.distinctAndSorted(List.of(4, 5, 6, 4, 2, 5, 9)));
    }

}
