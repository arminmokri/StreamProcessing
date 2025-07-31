package java_streams.reduction_and_aggregation.reduce_to_concatenate;


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
        assertEquals("abc", solution.reduceToConcatenate(List.of("a", "b", "c")));
    }

    @Test
    public void testEmptyList() {
        assertEquals("", solution.reduceToConcatenate(List.of()));
    }

    @Test
    public void testSingleElement() {
        assertEquals("x", solution.reduceToConcatenate(List.of("x")));
    }

    @Test
    public void testWithWhitespaceStrings() {
        assertEquals("a b", solution.reduceToConcatenate(List.of("a", " ", "b")));
    }

    @Test
    public void testWithEmptyStrings() {
        assertEquals("ab", solution.reduceToConcatenate(List.of("a", "", "b")));
    }

    @Test
    public void testAllEmptyStrings() {
        assertEquals("", solution.reduceToConcatenate(List.of("", "", "")));
    }

    @Test
    public void testLongConcatenation() {
        assertEquals("helloworldjava", solution.reduceToConcatenate(List.of("hello", "world", "java")));
    }
}
