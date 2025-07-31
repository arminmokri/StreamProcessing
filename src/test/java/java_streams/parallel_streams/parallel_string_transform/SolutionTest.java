package java_streams.parallel_streams.parallel_string_transform;


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
        assertEquals(List.of("A", "B", "C"), solution.parallelStringTransform(List.of("a", "b", "c")));
    }

    @Test
    public void testEmptyList() {
        assertEquals(List.of(), solution.parallelStringTransform(List.of()));
    }

    @Test
    public void testMixedCaseStrings() {
        assertEquals(List.of("HELLO", "WORLD"), solution.parallelStringTransform(List.of("Hello", "wOrLd")));
    }

    @Test
    public void testSpecialCharacters() {
        assertEquals(List.of("!@#", "$%^"), solution.parallelStringTransform(List.of("!@#", "$%^")));
    }

    @Test
    public void testDuplicates() {
        assertEquals(List.of("JAVA", "JAVA"), solution.parallelStringTransform(List.of("java", "java")));
    }

    @Test
    public void testSingleElement() {
        assertEquals(List.of("X"), solution.parallelStringTransform(List.of("x")));
    }

    @Test
    public void testWhitespaceAndEmptyStrings() {
        assertEquals(List.of(" ", ""), solution.parallelStringTransform(List.of(" ", "")));
    }

    @Test
    public void testNonEnglishCharacters() {
        assertEquals(List.of("É", "Ñ", "Ü"), solution.parallelStringTransform(List.of("é", "ñ", "ü")));
    }

}
