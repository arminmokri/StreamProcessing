package java_streams.sorting.sort_by_salary;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public List<Integer> sortBySalary(List<Integer> list) {
        return list.stream()
                .sorted(Comparator.naturalOrder())
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
        assertEquals(List.of(2000, 5000, 7000), solution.sortBySalary(List.of(5000, 2000, 7000)));
    }

    @Test
    public void testAlreadySortedList() {
        assertEquals(List.of(1000, 2000, 3000), solution.sortBySalary(List.of(1000, 2000, 3000)));
    }

    @Test
    public void testListWithDuplicates() {
        assertEquals(List.of(1000, 2000, 2000, 3000), solution.sortBySalary(List.of(2000, 1000, 3000, 2000)));
    }

    @Test
    public void testSingleElementList() {
        assertEquals(List.of(5000), solution.sortBySalary(List.of(5000)));
    }

    @Test
    public void testEmptyList() {
        assertEquals(List.of(), solution.sortBySalary(List.of()));
    }

}
