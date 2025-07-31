package java_streams.map_stream_operations.filter_map_by_key;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

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
                Map.of("two", 2, "three", 3),
                solution.filterMapByKey(
                        Map.of("one", 1, "two", 2, "three", 3), "t"
                )
        );
    }


}
