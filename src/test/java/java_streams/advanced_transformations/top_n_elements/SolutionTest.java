package java_streams.advanced_transformations.top_n_elements;


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
        assertEquals(List.of(50, 40, 30), solution.topNElements(List.of(10, 40, 30, 20, 50), 3));
    }

    @Test
    public void testTopTwo() {
        assertEquals(List.of(50, 40), solution.topNElements(List.of(10, 40, 30, 20, 50), 2));
    }

    @Test
    public void testTopOne() {
        assertEquals(List.of(100), solution.topNElements(List.of(100, 20, 30), 1));
    }

    @Test
    public void testTopAll() {
        assertEquals(List.of(50, 40, 30, 20, 10), solution.topNElements(List.of(10, 40, 30, 20, 50), 5));
    }

    @Test
    public void testNGreaterThanSize() {
        assertEquals(List.of(5, 4, 3, 2, 1), solution.topNElements(List.of(1, 2, 3, 4, 5), 10));
    }

    @Test
    public void testNIsZero() {
        assertEquals(List.of(), solution.topNElements(List.of(10, 20, 30), 0));
    }

    @Test
    public void testEmptyList() {
        assertEquals(List.of(), solution.topNElements(List.of(), 3));
    }

    @Test
    public void testWithDuplicates() {
        assertEquals(List.of(5, 5, 4), solution.topNElements(List.of(5, 3, 4, 5, 2), 3));
    }

    @Test
    public void testWithNegativeNumbers() {
        assertEquals(List.of(3, 2, 0), solution.topNElements(List.of(-1, 0, 2, -3, 3), 3));
    }

}
