package java_streams.exception_handling.handle_parseexception;


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
        assertEquals(
                List.of(10, 20, 30),
                solution.handleParseException(List.of("10", "abc", "20", "xyz", "30"))
        );
    }

    @Test
    public void testAllInvalid() {
        assertEquals(List.of(), solution.handleParseException(List.of("abc", "def", "xyz")));
    }

    @Test
    public void testAllValid() {
        assertEquals(List.of(1, 2, 3), solution.handleParseException(List.of("1", "2", "3")));
    }

    @Test
    public void testEmptyList() {
        assertEquals(List.of(), solution.handleParseException(List.of()));
    }

    @Test
    public void testNegativeAndZero() {
        assertEquals(List.of(0, -1, 100), solution.handleParseException(List.of("0", "-1", "100")));
    }

}
