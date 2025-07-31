package java_streams.collectors_and_conversions.join_names;


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
        assertEquals("Alice, Bob", solution.joinNames(List.of("Alice", "Bob")));
    }

    @Test
    public void testSingleElement() {
        assertEquals("Alice", solution.joinNames(List.of("Alice")));
    }

    @Test
    public void testEmptyList() {
        assertEquals("", solution.joinNames(List.of()));
    }

    @Test
    public void testMultipleNames() {
        assertEquals("Alice, Bob, Charlie, Dana",
                solution.joinNames(List.of("Alice", "Bob", "Charlie", "Dana")));
    }

}
