package java_streams.collectors_and_conversions.group_by_field;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    record Employee(long id, String name, float salary, String department) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Employee employee = (Employee) o;
            return id == employee.id
                    && Float.compare(employee.salary, salary) == 0
                    && Objects.equals(name, employee.name)
                    && Objects.equals(department, employee.department);
        }

    }

    public Map<String, List<Employee>> groupByField(List<Employee> list) {
        return list.stream()
                .collect(Collectors.groupingBy(Employee::department));
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
                Map.of("software",
                        List.of(new Solution.Employee(1, "John", 4000, "software"),
                                new Solution.Employee(3, "Andy", 6000, "software")
                        ),
                        "network",
                        List.of(new Solution.Employee(2, "Jane", 5000, "network"),
                                new Solution.Employee(4, "Micheal", 7000, "network")
                        )
                )
                ,
                solution.groupByField(
                        List.of(
                                new Solution.Employee(1, "John", 4000, "software"),
                                new Solution.Employee(2, "Jane", 5000, "network"),
                                new Solution.Employee(3, "Andy", 6000, "software"),
                                new Solution.Employee(4, "Micheal", 7000, "network")

                        ))
        );
    }

    @Test
    public void testSingleDepartment() {
        List<Solution.Employee> employees = List.of(
                new Solution.Employee(1, "Alice", 3000, "HR"),
                new Solution.Employee(2, "Bob", 3200, "HR")
        );

        assertEquals(
                Map.of("HR", employees),
                solution.groupByField(employees)
        );
    }

    @Test
    public void testEachEmployeeUniqueDepartment() {
        List<Solution.Employee> employees = List.of(
                new Solution.Employee(1, "Tom", 4000, "Sales"),
                new Solution.Employee(2, "Jerry", 4200, "Marketing")
        );

        assertEquals(
                Map.of(
                        "Sales", List.of(new Solution.Employee(1, "Tom", 4000, "Sales")),
                        "Marketing", List.of(new Solution.Employee(2, "Jerry", 4200, "Marketing"))
                ),
                solution.groupByField(employees)
        );
    }

}
