package java_streams.map_stream_operations.collect_map_to_list;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolutionTest {
    private static Solution solution;

    @BeforeAll
    public static void setUp() {
        solution = new Solution();
    }

    @Test
    public void testDefaultCase() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);

        List<Map.Entry<String, Integer>> expected = List.of(entry("a", 1), entry("b", 2));
        assertEquals(expected, solution.collectMapToList(map));
    }

    @Test
    public void testEmptyMap() {
        Map<String, Integer> map = Map.of();
        List<Map.Entry<String, Integer>> expected = List.of();
        assertEquals(expected, solution.collectMapToList(map));
    }

    @Test
    public void testSingleEntry() {
        Map<String, Integer> map = Map.of("key", 99);
        List<Map.Entry<String, Integer>> expected = List.of(entry("key", 99));
        assertEquals(expected, solution.collectMapToList(map));
    }

    @Test
    public void testPreservesOrder() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("x", 10);
        map.put("y", 20);
        map.put("z", 30);

        List<Map.Entry<String, Integer>> expected = List.of(
                entry("x", 10),
                entry("y", 20),
                entry("z", 30)
        );
        assertEquals(expected, solution.collectMapToList(map));
    }

    @Test
    public void testWithNegativeAndZeroValues() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("zero", 0);
        map.put("neg", -5);
        map.put("pos", 5);

        List<Map.Entry<String, Integer>> expected = List.of(
                entry("zero", 0),
                entry("neg", -5),
                entry("pos", 5)
        );
        assertEquals(expected, solution.collectMapToList(map));
    }

}
