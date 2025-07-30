package java_streams.custom_collectors.collector_with_downstream;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    record Item(String category, Integer price) {
    }

    public Map<String, Integer> collectorWithDownstream(List<Item> list) {
        return list.stream()
                .collect(
                        Collectors.groupingBy(
                                Item::category,
                                Collectors.summingInt(Item::price)
                        )
                );
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
        Map<String, Integer> expected = Map.of("A", 30, "B", 20, "C", 7);
        Map<String, Integer> actual = solution.collectorWithDownstream(
                List.of(
                        new Solution.Item("A", 10),
                        new Solution.Item("A", 20),
                        new Solution.Item("B", 5),
                        new Solution.Item("B", 15),
                        new Solution.Item("C", 7)
                )
        );
        assertEquals(expected, actual);
    }

    @Test
    public void testEmptyList() {
        assertEquals(
                Map.of(),
                solution.collectorWithDownstream(List.of())
        );
    }

    @Test
    public void testSingleCategory() {
        Map<String, Integer> expected = Map.of("X", 100);
        Map<String, Integer> actual = solution.collectorWithDownstream(
                List.of(
                        new Solution.Item("X", 40),
                        new Solution.Item("X", 60)
                )
        );
        assertEquals(expected, actual);
    }


}

