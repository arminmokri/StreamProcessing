package java_streams.reduction_and_aggregation.count_elements;


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
        assertEquals(4, solution.countElements(List.of(1, 2, 3, 4)));
    }

    @Test
    public void testEmptyList() {
        assertEquals(0, solution.countElements(List.of()));
    }

    @Test
    public void testSingleElement() {
        assertEquals(1, solution.countElements(List.of(42)));
    }

    @Test
    public void testDuplicates() {
        assertEquals(5, solution.countElements(List.of(1, 1, 1, 1, 1)));
    }

    @Test
    public void testNegativeAndZeroValues() {
        assertEquals(4, solution.countElements(List.of(-1, 0, -5, 3)));
    }

}
