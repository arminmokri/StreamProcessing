package java_streams.primitive_streams.boxing_unboxing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
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
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), solution.boxingUnboxing(IntStream.rangeClosed(1, 10)));
    }

    @Test
    public void testEmptyStream() {
        assertEquals(List.of(), solution.boxingUnboxing(IntStream.empty()));
    }

    @Test
    public void testNegativeRange() {
        assertEquals(List.of(-5, -4, -3, -2, -1), solution.boxingUnboxing(IntStream.rangeClosed(-5, -1)));
    }

    @Test
    public void testSingleElement() {
        assertEquals(List.of(7), solution.boxingUnboxing(IntStream.of(7)));
    }

    @Test
    public void testWithStepSizeLikeValues() {
        // Simulate step size by filtering
        assertEquals(List.of(2, 4, 6, 8, 10), solution.boxingUnboxing(IntStream.rangeClosed(1, 10).filter(i -> i % 2 == 0)));
    }

}
