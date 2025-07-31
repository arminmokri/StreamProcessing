package java_streams.optional_handling.default_if_empty;


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
                "No Match"
                , solution.defaultIfEmpty(
                        List.of("Alice", "Bob", "Charlie"), "Micheal"
                ));
    }

    @Test
    public void testMatchFound() {
        assertEquals(
                "Bob",
                solution.defaultIfEmpty(
                        List.of("Alice", "Bob", "Charlie"), "Bob"
                )
        );
    }

}
