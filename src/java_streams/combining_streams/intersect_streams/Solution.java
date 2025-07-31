package java_streams.combining_streams.intersect_streams;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public List<Integer> intersectStreams(List<Integer> list1, List<Integer> list2) {

        Set<Integer> set2 = Set.copyOf(list2);

        return list1.stream()
                .filter(set2::contains)
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

        List<Integer> list1 = List.of(1, 2, 3);
        List<Integer> list2 = List.of(2, 3, 4);

        List<Integer> expected = List.of(2, 3);

        assertEquals(expected, solution.intersectStreams(list1, list2));
    }

    @Test
    public void testNoIntersection() {
        List<Integer> list1 = List.of(1, 2, 3);
        List<Integer> list2 = List.of(4, 5, 6);

        List<Integer> expected = List.of();

        assertEquals(expected, solution.intersectStreams(list1, list2));
    }


    @Test
    public void testDuplicatesInFirstList() {
        List<Integer> list1 = List.of(1, 2, 2, 3);
        List<Integer> list2 = List.of(2, 3);

        List<Integer> expected = List.of(2, 2, 3);

        assertEquals(expected, solution.intersectStreams(list1, list2));
    }


    @Test
    public void testDuplicatesInSecondList() {
        List<Integer> list1 = List.of(1, 2, 3);
        List<Integer> list2 = List.of(2, 2, 3, 3);

        List<Integer> expected = List.of(2, 3);

        assertEquals(expected, solution.intersectStreams(list1, list2));
    }


    @Test
    public void testAllElementsIntersecting() {
        List<Integer> list1 = List.of(5, 6, 7);
        List<Integer> list2 = List.of(7, 6, 5);

        List<Integer> expected = List.of(5, 6, 7);

        assertEquals(expected, solution.intersectStreams(list1, list2));
    }


    @Test
    public void testOneListEmpty() {
        List<Integer> list1 = List.of();
        List<Integer> list2 = List.of(1, 2, 3);

        List<Integer> expected = List.of();

        assertEquals(expected, solution.intersectStreams(list1, list2));
    }


    @Test
    public void testBothListsEmpty() {
        List<Integer> list1 = List.of();
        List<Integer> list2 = List.of();

        List<Integer> expected = List.of();

        assertEquals(expected, solution.intersectStreams(list1, list2));
    }


}

