package java_streams.mapping_and_flatmapping.flatmap_nested_lists;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SolutionTest {
    private static Solution solution;

    @BeforeAll
    public static void setUp() {
        solution = new Solution();
    }


    @Test
    public void testDefaultCase() {
        assertEquals(List.of(1, 2, 3, 4, 5), solution.flatmapNestedLists(List.of(
                List.of(1, 2),
                List.of(3, 4),
                List.of(5)

        )));
    }

    @Test
    public void testEmptyOuterList() {
        assertEquals(List.of(), solution.flatmapNestedLists(List.of()));
    }

    @Test
    public void testEmptyInnerLists() {
        assertEquals(List.of(), solution.flatmapNestedLists(List.of(
                List.of(),
                List.of(),
                List.of()
        )));
    }

    @Test
    public void testMixedEmptyAndNonEmptyInnerLists() {
        assertEquals(List.of(10, 20, 30), solution.flatmapNestedLists(List.of(
                List.of(),
                List.of(10, 20),
                List.of(),
                List.of(30)
        )));
    }

    @Test
    public void testSingleNestedList() {
        assertEquals(List.of(7, 8, 9), solution.flatmapNestedLists(List.of(
                List.of(7, 8, 9)
        )));
    }

    @Test
    public void testNegativeNumbersAndDuplicates() {
        assertEquals(List.of(-1, 0, -1, 0, 5), solution.flatmapNestedLists(List.of(
                List.of(-1, 0),
                List.of(-1, 0),
                List.of(5)
        )));
    }
}
