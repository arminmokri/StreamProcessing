package java_streams.primitive_streams.longstream_generate;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public List<Long> longStreamGenerate(Integer count, Integer startingAt) {
        return LongStream
                .range(startingAt, startingAt + count)
                .boxed()
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
        assertEquals(List.of(10L, 11L, 12L), solution.longStreamGenerate(3, 10));
    }

    @Test
    public void testStartAtZero() {
        assertEquals(List.of(0L, 1L, 2L, 3L, 4L), solution.longStreamGenerate(5, 0));
    }

    @Test
    public void testSingleElement() {
        assertEquals(List.of(100L), solution.longStreamGenerate(1, 100));
    }

    @Test
    public void testNegativeStart() {
        assertEquals(List.of(-5L, -4L, -3L), solution.longStreamGenerate(3, -5));
    }

    @Test
    public void testZeroCount() {
        assertEquals(List.of(), solution.longStreamGenerate(0, 50));
    }

    @Test
    public void testLargeCount() {
        List<Long> expected = LongStream.range(1000, 1010).boxed().collect(Collectors.toList());
        assertEquals(expected, solution.longStreamGenerate(10, 1000));
    }

}
