package java_streams.reduction_and_aggregation.find_max_salary;

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
        assertEquals(9000, solution.findMaxSalary(List.of(5000, 7000, 9000)));
    }

    @Test
    public void testEmptyList() {
        assertEquals(0, solution.findMaxSalary(List.of()));
    }

    @Test
    public void testNegativeSalaries() {
        assertEquals(-1000, solution.findMaxSalary(List.of(-5000, -3000, -1000)));
    }

    @Test
    public void testSingleSalary() {
        assertEquals(7500, solution.findMaxSalary(List.of(7500)));
    }

    @Test
    public void testAllSameSalaries() {
        assertEquals(8000, solution.findMaxSalary(List.of(8000, 8000, 8000)));
    }


}
