package java_streams.basic_stream_operations.skip_and_limit;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public List<Integer> skipAndLimit(List<Integer> list, int skip, int limit) {
        return list.stream()
                .skip(skip)
                .limit(limit)
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
        assertEquals(List.of(20, 30, 40), solution.skipAndLimit(List.of(10, 20, 30, 40, 50), 1, 3));
    }

    @Test
    public void testSkipZero() {
        assertEquals(List.of(10, 20, 30), solution.skipAndLimit(List.of(10, 20, 30, 40), 0, 3));
    }

    @Test
    public void testLimitZero() {
        assertEquals(List.of(), solution.skipAndLimit(List.of(10, 20, 30), 1, 0));
    }

    @Test
    public void testSkipBeyondSize() {
        assertEquals(List.of(), solution.skipAndLimit(List.of(1, 2, 3), 5, 2));
    }

    @Test
    public void testLimitBeyondSize() {
        assertEquals(List.of(2, 3), solution.skipAndLimit(List.of(1, 2, 3), 1, 10));
    }

    @Test
    public void testSkipAndLimitExceedSize() {
        assertEquals(List.of(3), solution.skipAndLimit(List.of(1, 2, 3), 2, 5));
    }

    @Test
    public void testEmptyList() {
        assertEquals(List.of(), solution.skipAndLimit(List.of(), 2, 3));
    }
}
