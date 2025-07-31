package java_streams.collectors_and_conversions.count_grouped;


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
                Map.of(85f, 2L, 76f, 2L),
                solution.groupByField(
                        List.of(
                                new Solution.Student(1, "John", 85f),
                                new Solution.Student(2, "Jane", 76f),
                                new Solution.Student(3, "Andy", 85f),
                                new Solution.Student(4, "Micheal", 76f)

                        ))
        );
    }

    @Test
    public void testEmptyList() {
        assertEquals(Map.of(), solution.groupByField(List.of()));
    }

    @Test
    public void testSingleStudent() {
        assertEquals(
                Map.of(90f, 1L),
                solution.groupByField(List.of(new Solution.Student(1, "Alice", 90)))
        );
    }

    @Test
    public void testAllDifferentGrades() {
        assertEquals(
                Map.of(70f, 1L, 80f, 1L, 90f, 1L),
                solution.groupByField(List.of(
                        new Solution.Student(1, "Tom", 70),
                        new Solution.Student(2, "Jerry", 80),
                        new Solution.Student(3, "Spike", 90)
                ))
        );
    }


}
