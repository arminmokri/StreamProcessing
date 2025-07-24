package java_streams.collectors_and_conversions.collect_to_set;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {


    public Set<String> collectToSet(List<String> list) {
        return list.stream()
                .collect(Collectors.toSet());
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
        assertEquals(Set.of("a", "b"), solution.collectToSet(List.of("a", "b", "a")));
    }

    @Test
    public void testEmptyList() {
        assertEquals(Set.of(), solution.collectToSet(List.of()));
    }

    @Test
    public void testSingleElementList() {
        assertEquals(Set.of("x"), solution.collectToSet(List.of("x")));
    }

    @Test
    public void testAllDuplicates() {
        assertEquals(Set.of("z"), solution.collectToSet(List.of("z", "z", "z")));
    }
}
