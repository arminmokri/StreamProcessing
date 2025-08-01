package java_streams.combining_streams.concat_two_streams;


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
        List<Integer> list2 = List.of(3, 4);

        List<Integer> expected = List.of(1, 2, 3, 4);

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }

    @Test
    public void testBothListsEmpty() {
        List<Integer> list1 = List.of();
        List<Integer> list2 = List.of();

        List<Integer> expected = List.of();

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }

    @Test
    public void testFirstListEmpty() {
        List<Integer> list1 = List.of();
        List<Integer> list2 = List.of(5, 6);

        List<Integer> expected = List.of(5, 6);

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }

    @Test
    public void testSecondListEmpty() {
        List<Integer> list1 = List.of(7, 8);
        List<Integer> list2 = List.of();

        List<Integer> expected = List.of(7, 8);

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }

    @Test
    public void testWithDuplicates() {
        List<Integer> list1 = List.of(1, 2, 2);
        List<Integer> list2 = List.of(2, 3);

        List<Integer> expected = List.of(1, 2, 2, 2, 3);

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }

    @Test
    public void testWithNegativeNumbers() {
        List<Integer> list1 = List.of(-1, -2);
        List<Integer> list2 = List.of(3, 4);

        List<Integer> expected = List.of(-1, -2, 3, 4);

        assertEquals(expected, solution.concatTwoStreams(list1, list2));
    }


}

