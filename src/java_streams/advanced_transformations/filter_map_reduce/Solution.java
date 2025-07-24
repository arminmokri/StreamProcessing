package java_streams.advanced_transformations.filter_map_reduce;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public Integer filterMapReduce(List<Integer> list) {
        return list.stream()
                .filter(i -> i % 2 == 0)
                .map(i -> i * i)
                .reduce(Integer::sum)
                .orElse(0);
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
        assertEquals(20, solution.filterMapReduce(List.of(1, 2, 3, 4, 5)));
    }

    @Test
    public void testEmptyList() {
        assertEquals(0, solution.filterMapReduce(List.of()));
    }

    @Test
    public void testNoEvenNumbers() {
        assertEquals(0, solution.filterMapReduce(List.of(1, 3, 5, 7)));
    }

    @Test
    public void testAllEvenNumbers() {
        assertEquals(56, solution.filterMapReduce(List.of(2, 4, 6))); // 4 + 16 + 36 = 56
    }

    @Test
    public void testWithNegativeNumbers() {
        assertEquals(20, solution.filterMapReduce(List.of(-4, -3, 1, 2))); // (-4)^2 + 2^2 = 16 + 4 = 20
    }

    @Test
    public void testLargeNumbers() {
        assertEquals(100000000, solution.filterMapReduce(List.of(10000))); // 10000^2 = 100000000
    }

    @Test
    public void testRepeatedEvenNumbers() {
        assertEquals(12, solution.filterMapReduce(List.of(2, 2, 2))); // 3 * 4 = 12
    }

    @Test
    public void testSingleEvenNumber() {
        assertEquals(36, solution.filterMapReduce(List.of(6)));
    }

}
