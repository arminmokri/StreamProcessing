package java_streams.map_stream_operations.sort_map_by_value;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
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
                Map.of("y", 10, "z", 20, "x", 30),
                solution.sortMapByValue(
                        Map.of("x", 30, "y", 10, "z", 20)
                )
        );
    }

    @Test
    public void testEmptyMap() {
        assertEquals(Map.of(), solution.sortMapByValue(Map.of()));
    }

    @Test
    public void testSingleEntry() {
        Map<String, Integer> expected = new LinkedHashMap<>();
        expected.put("a", 1);
        assertEquals(expected, solution.sortMapByValue(Map.of("a", 1)));
    }

    @Test
    public void testAllValuesEqual() {
        Map<String, Integer> input = Map.of("a", 5, "b", 5, "c", 5);
        Map<String, Integer> expected = new LinkedHashMap<>();
        expected.put("a", 5);
        expected.put("b", 5);
        expected.put("c", 5);
        assertEquals(expected, solution.sortMapByValue(input));
    }

    @Test
    public void testNegativeValues() {
        Map<String, Integer> input = Map.of("a", -1, "b", -10, "c", 0);
        Map<String, Integer> expected = new LinkedHashMap<>();
        expected.put("b", -10);
        expected.put("a", -1);
        expected.put("c", 0);
        assertEquals(expected, solution.sortMapByValue(input));
    }

    @Test
    public void testAlreadySorted() {
        Map<String, Integer> input = new LinkedHashMap<>();
        input.put("a", 1);
        input.put("b", 2);
        input.put("c", 3);

        Map<String, Integer> expected = new LinkedHashMap<>();
        expected.put("a", 1);
        expected.put("b", 2);
        expected.put("c", 3);

        assertEquals(expected, solution.sortMapByValue(input));
    }


}
