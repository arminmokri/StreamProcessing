package java_streams.map_stream_operations.merge_maps;


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
                Map.of("a", 1, "b", 5, "c", 4),
                solution.mergeMaps(
                        Map.of("a", 1, "b", 2),
                        Map.of("b", 3, "c", 4)
                )
        );
    }

    @Test
    public void testEmptyMaps() {
        assertEquals(Map.of(), solution.mergeMaps(Map.of(), Map.of()));
    }

    @Test
    public void testOneEmptyMap() {
        assertEquals(Map.of("a", 2), solution.mergeMaps(Map.of("a", 2), Map.of()));
        assertEquals(Map.of("b", 5), solution.mergeMaps(Map.of(), Map.of("b", 5)));
    }

    @Test
    public void testNoOverlap() {
        assertEquals(
                Map.of("a", 1, "b", 2),
                solution.mergeMaps(Map.of("a", 1), Map.of("b", 2))
        );
    }

    @Test
    public void testAllKeysOverlap() {
        assertEquals(
                Map.of("x", 15, "y", 30),
                solution.mergeMaps(Map.of("x", 5, "y", 10), Map.of("x", 10, "y", 20))
        );
    }

    @Test
    public void testNegativeValues() {
        assertEquals(
                Map.of("a", -3, "b", 1),
                solution.mergeMaps(Map.of("a", -1, "b", 2), Map.of("a", -2, "b", -1))
        );
    }

}
