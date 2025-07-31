package java_streams.combining_streams.union_streams;


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

        List<Integer> list1 = List.of(1, 2);
        List<Integer> list2 = List.of(2, 3);

        List<Integer> expected = List.of(1, 2, 3);

        assertEquals(expected, solution.unionStreams(list1, list2));
    }

    @Test
    public void testDuplicatesInFirstList() {
        List<Integer> list1 = List.of(1, 1, 2);
        List<Integer> list2 = List.of(3);

        List<Integer> expected = List.of(1, 2, 3);

        assertEquals(expected, solution.unionStreams(list1, list2));
    }


    @Test
    public void testDuplicatesInBothLists() {
        List<Integer> list1 = List.of(1, 2, 2);
        List<Integer> list2 = List.of(2, 3, 3);

        List<Integer> expected = List.of(1, 2, 3);

        assertEquals(expected, solution.unionStreams(list1, list2));
    }


    @Test
    public void testOneListEmpty() {
        List<Integer> list1 = List.of();
        List<Integer> list2 = List.of(4, 5);

        List<Integer> expected = List.of(4, 5);

        assertEquals(expected, solution.unionStreams(list1, list2));
    }

    @Test
    public void testBothListsEmpty() {
        List<Integer> list1 = List.of();
        List<Integer> list2 = List.of();

        List<Integer> expected = List.of();

        assertEquals(expected, solution.unionStreams(list1, list2));
    }

    @Test
    public void testAllElementsDuplicated() {
        List<Integer> list1 = List.of(1, 2);
        List<Integer> list2 = List.of(1, 2);

        List<Integer> expected = List.of(1, 2);

        assertEquals(expected, solution.unionStreams(list1, list2));
    }


}

