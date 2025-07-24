package java_streams.mapping_and_flatmapping.parse_csv_to_object;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    static class Employee {
        private long id;
        private String name;
        private float salary;

        public Employee(long id, String name, float salary) {
            this.id = id;
            this.name = name;
            this.salary = salary;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public float getSalary() {
            return salary;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Employee employee = (Employee) o;
            return id == employee.id && Float.compare(employee.salary, salary) == 0 && Objects.equals(name, employee.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, salary);
        }
    }

    public List<Employee> parseCSVToObject(List<String> list) {
        return list
                .stream()
                .map(item -> item.split(","))
                .filter(parts -> parts.length == 3)
                .map(parts ->
                        new Employee(
                                Long.parseLong(parts[0]),
                                parts[1],
                                Float.parseFloat(parts[2])
                        )
                )
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
        assertEquals(
                List.of(
                        new Solution.Employee(1, "John", 5000),
                        new Solution.Employee(2, "Jane", 6000)
                ),
                solution.parseCSVToObject(List.of("1,John,5000", "2,Jane,6000")));
    }

    @Test
    public void testEmptyInputList() {
        assertEquals(List.of(), solution.parseCSVToObject(List.of()));
    }

    @Test
    public void testInvalidFormat_MissingField() {
        // Should ignore the invalid line
        assertEquals(
                List.of(new Solution.Employee(3, "Alice", 7000)),
                solution.parseCSVToObject(List.of("3,Alice,7000", "4,Bob"))
        );
    }

    @Test
    public void testInvalidFormat_ExtraField() {
        // Should ignore the invalid line with 4 fields
        assertEquals(
                List.of(new Solution.Employee(5, "Charlie", 8000)),
                solution.parseCSVToObject(List.of("5,Charlie,8000", "6,Dave,9000,Extra"))
        );
    }

    @Test
    public void testInvalidNumberFormat() {
        // This will throw NumberFormatException unless handled
        try {
            solution.parseCSVToObject(List.of("abc,John,5000"));
        } catch (NumberFormatException e) {
            // Expected exception for this test
            assertEquals(NumberFormatException.class, e.getClass());
        }
    }

    @Test
    public void testWhitespaceHandling() {
        // Will currently parse as-is, including spaces
        assertEquals(
                List.of(new Solution.Employee(7, "Eve", 10000)),
                solution.parseCSVToObject(List.of("7,Eve,10000"))
        );
    }

    @Test
    public void testDuplicateEntries() {
        Solution.Employee emp = new Solution.Employee(8, "Sam", 5500);
        assertEquals(
                List.of(emp, emp),
                solution.parseCSVToObject(List.of("8,Sam,5500", "8,Sam,5500"))
        );
    }


}
