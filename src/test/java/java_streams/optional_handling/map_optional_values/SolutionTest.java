package java_streams.optional_handling.map_optional_values;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

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
                Optional.of("JOHN")
                , solution.mapOptionalValues(Optional.of("john"))
        );
    }

    @Test
    public void testEmptyOptional() {
        assertEquals(
                Optional.empty(),
                solution.mapOptionalValues(Optional.empty())
        );
    }

}
