package java_streams.basic_stream_operations.distinct_elements;


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
        assertEquals(List.of(1, 2, 3), solution.distinctElements(List.of(1, 2, 2, 3, 3, 3)));
    }

    @Test
    public void testEmptyList() {
        assertEquals(List.of(), solution.distinctElements(List.of()));
    }

    @Test
    public void testAllUnique() {
        assertEquals(List.of(4, 5, 6), solution.distinctElements(List.of(4, 5, 6)));
    }

    @Test
    public void testAllDuplicates() {
        assertEquals(List.of(7), solution.distinctElements(List.of(7, 7, 7, 7)));
    }

    @Test
    public void testNegativeAndZeroValues() {
        assertEquals(List.of(0, -1, -2, 2), solution.distinctElements(List.of(0, -1, -1, -2, 2, 0, 2)));
    }

    @Test
    public void testOrderIsPreserved() {
        assertEquals(List.of(10, 20, 30, 40), solution.distinctElements(List.of(10, 20, 30, 20, 10, 40)));
    }


}

