package java_streams.sorting.sort_custom_objects;


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
        assertEquals(List.of("fig", "apple", "banana"), solution.sortCustomObjects(List.of("apple", "fig", "banana")));
    }

    @Test
    public void testEmptyList() {
        assertEquals(List.of(), solution.sortCustomObjects(List.of()));
    }

    @Test
    public void testSingleElement() {
        assertEquals(List.of("pear"), solution.sortCustomObjects(List.of("pear")));
    }

    @Test
    public void testSameLengthStrings() {
        assertEquals(List.of("bat", "dog", "cat"), solution.sortCustomObjects(List.of("bat", "dog", "cat")));
    }

    @Test
    public void testDuplicates() {
        assertEquals(List.of("fig", "fig", "apple", "banana", "cherry"),
                solution.sortCustomObjects(List.of("banana", "fig", "apple", "cherry", "fig")));
    }

    @Test
    public void testAlreadySortedByLength() {
        assertEquals(List.of("a", "to", "dog", "bear"), solution.sortCustomObjects(List.of("a", "to", "dog", "bear")));
    }

    @Test
    public void testReverseLengthOrder() {
        assertEquals(List.of("a", "to", "dog", "bear"), solution.sortCustomObjects(List.of("bear", "dog", "to", "a")));
    }


}
