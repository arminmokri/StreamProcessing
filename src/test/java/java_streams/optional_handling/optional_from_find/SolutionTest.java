package java_streams.optional_handling.optional_from_find;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
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
                Optional.of(6)
                , solution.optionalFromFind(
                        List.of(1, 3, 5, 6, 7, 9)
                ));
    }

    @Test
    public void testEmptyList() {
        assertEquals(Optional.empty(), solution.optionalFromFind(List.of(1, 3, 5, 7, 9)));
    }


}
