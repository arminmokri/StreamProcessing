package java_streams.primitive_streams.intstream_range;


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
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), solution.intStreamRange(1, 10));
    }

    @Test
    public void testSingleElementRange() {
        assertEquals(List.of(5), solution.intStreamRange(5, 5));
    }

    @Test
    public void testNegativeRange() {
        assertEquals(List.of(-3, -2, -1, 0, 1), solution.intStreamRange(-3, 1));
    }

    @Test
    public void testZeroToZero() {
        assertEquals(List.of(0), solution.intStreamRange(0, 0));
    }

    @Test
    public void testDescendingRangeShouldReturnEmpty() {
        assertEquals(List.of(), solution.intStreamRange(10, 5));
    }

    @Test
    public void testFullNegativeRange() {
        assertEquals(List.of(-10, -9, -8, -7, -6), solution.intStreamRange(-10, -6));
    }

}
