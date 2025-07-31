package java_streams.optional_handling.filter_optional;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class Solution {

    public Optional<Integer> filterOptional(Optional<Integer> optional, Integer biggerThan) {
        return optional.filter(i -> i > biggerThan);

    }
}


class SolutionTest {
    private static Solution solution;

    @BeforeAll
    public static void setUp() {
        solution = new Solution();
    }

    @Test
    public void testDefaultCase() {
        assertEquals(
                Optional.empty()
                , solution.filterOptional(Optional.of(10), 15)
        );
    }

    @Test
    public void testValuePassesFilter() {
        assertEquals(
                Optional.of(20),
                solution.filterOptional(Optional.of(20), 15)
        );
    }

    @Test
    public void testEmptyOptionalInput() {
        assertEquals(
                Optional.empty(),
                solution.filterOptional(Optional.empty(), 15)
        );
    }

}
