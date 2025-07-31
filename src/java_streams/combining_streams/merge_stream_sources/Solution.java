package java_streams.combining_streams.merge_stream_sources;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public List<Integer> mergeStreamSources(List<Integer> list1, List<Integer> list2) {
        return Stream
                .concat(list1.stream(), list2.stream())
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

        List<Integer> list1 = List.of(10, 20);
        List<Integer> list2 = List.of(20, 30);

        List<Integer> expected = List.of(10, 20, 20, 30);

        assertEquals(expected, solution.mergeStreamSources(list1, list2));
    }


    @Test
    public void testBothListsEmpty() {
        List<Integer> list1 = List.of();
        List<Integer> list2 = List.of();

        List<Integer> expected = List.of();

        assertEquals(expected, solution.mergeStreamSources(list1, list2));
    }

    @Test
    public void testFirstListEmpty() {
        List<Integer> list1 = List.of();
        List<Integer> list2 = List.of(5, 6);

        List<Integer> expected = List.of(5, 6);

        assertEquals(expected, solution.mergeStreamSources(list1, list2));
    }

    @Test
    public void testSecondListEmpty() {
        List<Integer> list1 = List.of(7, 8);
        List<Integer> list2 = List.of();

        List<Integer> expected = List.of(7, 8);

        assertEquals(expected, solution.mergeStreamSources(list1, list2));
    }

    @Test
    public void testWithDuplicates() {
        List<Integer> list1 = List.of(1, 2, 2);
        List<Integer> list2 = List.of(2, 3);

        List<Integer> expected = List.of(1, 2, 2, 2, 3);

        assertEquals(expected, solution.mergeStreamSources(list1, list2));
    }

    @Test
    public void testWithNegativeNumbers() {
        List<Integer> list1 = List.of(-1, -2);
        List<Integer> list2 = List.of(3, 4);

        List<Integer> expected = List.of(-1, -2, 3, 4);

        assertEquals(expected, solution.mergeStreamSources(list1, list2));
    }

}

