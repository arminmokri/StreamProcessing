package java_streams.sorting.reverse_sort;


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
    public void testDefaultCase() {
        assertEquals(List.of(5, 4, 2, 1), solution.reverseSort(List.of(1, 4, 2, 5)));
    }

    @Test
    public void testEmptyList() {
        assertEquals(List.of(), solution.reverseSort(List.of()));
    }

    @Test
    public void testSingleElement() {
        assertEquals(List.of(7), solution.reverseSort(List.of(7)));
    }

    @Test
    public void testWithDuplicates() {
        assertEquals(List.of(5, 5, 3, 2, 2, 1), solution.reverseSort(List.of(2, 5, 3, 5, 1, 2)));
    }

    @Test
    public void testAllElementsSame() {
        assertEquals(List.of(4, 4, 4, 4), solution.reverseSort(List.of(4, 4, 4, 4)));
    }

    @Test
    public void testAlreadyDescending() {
        assertEquals(List.of(9, 7, 5, 3), solution.reverseSort(List.of(9, 7, 5, 3)));
    }

    @Test
    public void testAscendingOrder() {
        assertEquals(List.of(4, 3, 2, 1), solution.reverseSort(List.of(1, 2, 3, 4)));
    }

}
