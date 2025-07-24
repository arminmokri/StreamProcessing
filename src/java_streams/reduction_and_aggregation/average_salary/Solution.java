package java_streams.reduction_and_aggregation.average_salary;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public Double averageSalary(List<Integer> list) {
        return list.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
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
        assertEquals(7000.0, solution.averageSalary(List.of(5000, 7000, 9000)));
    }

    @Test
    public void testEmptyList() {
        assertEquals(0.0, solution.averageSalary(List.of()));
    }

    @Test
    public void testSingleElement() {
        assertEquals(4500.0, solution.averageSalary(List.of(4500)));
    }

    @Test
    public void testAllSameSalaries() {
        assertEquals(6000.0, solution.averageSalary(List.of(6000, 6000, 6000)));
    }

    @Test
    public void testWithZeroSalaries() {
        assertEquals(0.0, solution.averageSalary(List.of(0, 0, 0)));
    }

    @Test
    public void testLargeSalaries() {
        assertEquals(1_500_000.0, solution.averageSalary(List.of(1_000_000, 2_000_000)));
    }

    @Test
    public void testWithNegativeSalaries() {
        assertEquals(-3000.0, solution.averageSalary(List.of(-1000, -2000, -6000)));
    }

}
