package java_streams.map_stream_operations.filter_map_by_key;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class Solution {

    public Map<String, Integer> filterMapByKey(Map<String, Integer> map, String startsWith) {
        return map
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(startsWith))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
                Map.of("two", 2, "three", 3),
                solution.filterMapByKey(
                        Map.of("one", 1, "two", 2, "three", 3), "t"
                )
        );
    }


}
