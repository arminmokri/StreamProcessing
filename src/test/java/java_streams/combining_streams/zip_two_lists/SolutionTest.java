package java_streams.combining_streams.zip_two_lists;


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

        List<String> list1 = List.of("a", "b");
        List<Integer> list2 = List.of(1, 2);

        List<Map.Entry<String, Integer>> expected = List.of(
                Map.entry("a", 1),
                Map.entry("b", 2)
        );

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }

    @Test
    public void testFirstListLonger() {
        List<String> list1 = List.of("a", "b", "c");
        List<Integer> list2 = List.of(1, 2);

        List<Map.Entry<String, Integer>> expected = List.of(
                Map.entry("a", 1),
                Map.entry("b", 2)
        );

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }

    @Test
    public void testSecondListLonger() {
        List<String> list1 = List.of("a");
        List<Integer> list2 = List.of(1, 2, 3);

        List<Map.Entry<String, Integer>> expected = List.of(
                Map.entry("a", 1)
        );

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }

    @Test
    public void testEmptyLists() {
        List<String> list1 = List.of();
        List<Integer> list2 = List.of();

        List<Map.Entry<String, Integer>> expected = List.of();

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }

    @Test
    public void testSingleElements() {
        List<String> list1 = List.of("x");
        List<Integer> list2 = List.of(99);

        List<Map.Entry<String, Integer>> expected = List.of(
                Map.entry("x", 99)
        );

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }

    @Test
    public void testWithDuplicates() {
        List<String> list1 = List.of("a", "a");
        List<Integer> list2 = List.of(1, 1);

        List<Map.Entry<String, Integer>> expected = List.of(
                Map.entry("a", 1),
                Map.entry("a", 1)
        );

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }


}

