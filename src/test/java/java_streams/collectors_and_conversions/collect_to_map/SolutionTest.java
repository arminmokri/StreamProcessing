package java_streams.collectors_and_conversions.collect_to_map;


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
        assertEquals(
                Map.of(1L, 5000f, 2L, 6000f)
                ,
                solution.collectToMap(
                        List.of(
                                new Solution.Employee(1, "John", 5000),
                                new Solution.Employee(2, "Jane", 6000)

                        )));
    }

    @Test
    public void testEmptyList() {
        assertEquals(Map.of(), solution.collectToMap(List.of()));
    }

    @Test
    public void testSingleEmployee() {
        assertEquals(Map.of(1L, 7000f),
                solution.collectToMap(List.of(new Solution.Employee(1, "Alice", 7000)))
        );
    }

    @Test
    public void testDuplicateIdsThrowsException() {
        List<Solution.Employee> list = List.of(
                new Solution.Employee(1, "Alice", 7000),
                new Solution.Employee(1, "Bob", 8000)
        );
        try {
            solution.collectToMap(list);
        } catch (IllegalStateException e) {
            // expected behavior
            return;
        }
        throw new AssertionError("Expected IllegalStateException due to duplicate keys");
    }

}
