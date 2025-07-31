package java_streams.primitive_streams.doublestream_average;


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
        assertEquals(3.33D, solution.doubleStreamAverage(List.of(2.5, 3.5, 4.0)), 0.01);
    }

    @Test
    public void testEmptyList() {
        assertEquals(0.0, solution.doubleStreamAverage(List.of()), 0.0001);
    }

    @Test
    public void testSingleElement() {
        assertEquals(5.0, solution.doubleStreamAverage(List.of(5.0)), 0.0001);
    }

    @Test
    public void testNegativeNumbers() {
        assertEquals(-2.0, solution.doubleStreamAverage(List.of(-1.0, -2.0, -3.0)), 0.0001);
    }


}
