package java_streams.collectors_and_conversions.collect_to_map;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    record Employee(long id, String name, float salary) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Employee employee = (Employee) o;
            return id == employee.id && Float.compare(employee.salary, salary) == 0 && Objects.equals(name, employee.name);
        }

    }

    public Map<Long, Float> collectToMap(List<Employee> list) {
        return list.stream()
                .collect(Collectors.toMap(
                                Employee::id,
                                Employee::salary
                        )
                );
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
        assertEquals(
                Map.of(1L, 5000f, 2L, 6000f)
                ,
                solution.collectToMap(
                        List.of(
                                new Solution.Employee(1, "John", 5000),
                                new Solution.Employee(2, "Jane", 6000)

                        )));
    }

    @Test
    public void testEmptyList() {
        assertEquals(Map.of(), solution.collectToMap(List.of()));
    }

    @Test
    public void testSingleEmployee() {
        assertEquals(Map.of(1L, 7000f),
                solution.collectToMap(List.of(new Solution.Employee(1, "Alice", 7000)))
        );
    }

    @Test
    public void testDuplicateIdsThrowsException() {
        List<Solution.Employee> list = List.of(
                new Solution.Employee(1, "Alice", 7000),
                new Solution.Employee(1, "Bob", 8000)
        );
        try {
            solution.collectToMap(list);
        } catch (IllegalStateException e) {
            // expected behavior
            return;
        }
        throw new AssertionError("Expected IllegalStateException due to duplicate keys");
    }

}
