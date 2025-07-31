package java_streams.custom_collectors.collector_for_partitioned_lists;


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

        Map<Boolean, List<Integer>> expected = Map.of(Boolean.TRUE, List.of(2, 4, 6), Boolean.FALSE, List.of(1, 3, 5));
        Map<Boolean, List<Integer>> actual = solution.collectorForPartitionedLists(
                List.of(1, 2, 3, 4, 5, 6)
        );
        assertEquals(expected, actual);
    }

    @Test
    public void testEmptyList() {
        Map<Boolean, List<Integer>> expected = Map.of(
                Boolean.TRUE, List.of(),
                Boolean.FALSE, List.of()
        );
        Map<Boolean, List<Integer>> actual = solution.collectorForPartitionedLists(List.of());
        assertEquals(expected, actual);
    }

    @Test
    public void testAllEven() {
        Map<Boolean, List<Integer>> expected = Map.of(
                Boolean.TRUE, List.of(2, 4, 6),
                Boolean.FALSE, List.of()
        );
        Map<Boolean, List<Integer>> actual = solution.collectorForPartitionedLists(List.of(2, 4, 6));
        assertEquals(expected, actual);
    }

    @Test
    public void testAllOdd() {
        Map<Boolean, List<Integer>> expected = Map.of(
                Boolean.TRUE, List.of(),
                Boolean.FALSE, List.of(1, 3, 5)
        );
        Map<Boolean, List<Integer>> actual = solution.collectorForPartitionedLists(List.of(1, 3, 5));
        assertEquals(expected, actual);
    }


}

