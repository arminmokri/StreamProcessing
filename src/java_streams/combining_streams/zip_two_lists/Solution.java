package java_streams.combining_streams.zip_two_lists;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public List<AbstractMap.SimpleEntry<String, Integer>> concatTwoStreams(List<String> list1, List<Integer> list2) {
        return IntStream
                .range(0, Math.min(list1.size(), list2.size()))
                .mapToObj(i -> new AbstractMap.SimpleEntry<>(list1.get(i), list2.get(i)))
                .collect(Collectors.toList());
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

        List<String> list1 = List.of("a", "b");
        List<Integer> list2 = List.of(1, 2);

        List<AbstractMap.SimpleEntry<String, Integer>> expected = List.of(
                new AbstractMap.SimpleEntry<>("a", 1),
                new AbstractMap.SimpleEntry<>("b", 2)
        );

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }

    @Test
    public void testFirstListLonger() {
        List<String> list1 = List.of("a", "b", "c");
        List<Integer> list2 = List.of(1, 2);

        List<AbstractMap.SimpleEntry<String, Integer>> expected = List.of(
                new AbstractMap.SimpleEntry<>("a", 1),
                new AbstractMap.SimpleEntry<>("b", 2)
        );

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }

    @Test
    public void testSecondListLonger() {
        List<String> list1 = List.of("a");
        List<Integer> list2 = List.of(1, 2, 3);

        List<AbstractMap.SimpleEntry<String, Integer>> expected = List.of(
                new AbstractMap.SimpleEntry<>("a", 1)
        );

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }

    @Test
    public void testEmptyLists() {
        List<String> list1 = List.of();
        List<Integer> list2 = List.of();

        List<AbstractMap.SimpleEntry<String, Integer>> expected = List.of();

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }

    @Test
    public void testSingleElements() {
        List<String> list1 = List.of("x");
        List<Integer> list2 = List.of(99);

        List<AbstractMap.SimpleEntry<String, Integer>> expected = List.of(
                new AbstractMap.SimpleEntry<>("x", 99)
        );

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }

    @Test
    public void testWithDuplicates() {
        List<String> list1 = List.of("a", "a");
        List<Integer> list2 = List.of(1, 1);

        List<AbstractMap.SimpleEntry<String, Integer>> expected = List.of(
                new AbstractMap.SimpleEntry<>("a", 1),
                new AbstractMap.SimpleEntry<>("a", 1)
        );

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }


}

