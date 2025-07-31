package java_streams.custom_collectors.collector_for_frequency_map;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
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

        Map<String, Integer> expected = Map.of("apple", 3, "banana", 2, "orange", 1);
        Map<String, Integer> actual = solution.collectorForFrequencyMap(
                List.of("apple", "banana", "apple", "orange", "banana", "apple")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void testEmptyList() {
        Map<String, Integer> expected = Map.of();
        Map<String, Integer> actual = solution.collectorForFrequencyMap(List.of());
        assertEquals(expected, actual);
    }

    @Test
    public void testSingleElement() {
        Map<String, Integer> expected = Map.of("kiwi", 4);
        Map<String, Integer> actual = solution.collectorForFrequencyMap(
                List.of("kiwi", "kiwi", "kiwi", "kiwi")
        );
        assertEquals(expected, actual);
    }

    @Test
    public void testCaseSensitivity() {
        Map<String, Integer> expected = Map.of("Apple", 2, "apple", 1);
        Map<String, Integer> actual = solution.collectorForFrequencyMap(
                List.of("Apple", "apple", "Apple")
        );
        assertEquals(expected, actual);
    }

}

