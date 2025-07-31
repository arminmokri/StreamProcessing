package java_streams.debugging_with_peek.side_effects;


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
        List<Integer> input = List.of(1, 2, 3);
        List<Integer> expected = List.of(2, 4, 6);

        List<Integer> actual = solution.sideEffects(input);

        assertEquals(expected, actual);
    }

    @Test
    public void testEmptyList() {
        List<Integer> input = List.of();
        List<Integer> expected = List.of();

        assertEquals(expected, solution.sideEffects(input));
    }

    @Test
    public void testWithNegativeNumbers() {
        List<Integer> input = List.of(-1, -2, 0);
        List<Integer> expected = List.of(-2, -4, 0);

        assertEquals(expected, solution.sideEffects(input));
    }


}
