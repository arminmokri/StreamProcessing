package java_streams.collectors_and_conversions.collect_to_map;


import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    // [E(1, "John", 5000), E(2, "Jane", 6000)] ->
    // {1L, 5000f, 2L, 6000f}
    public Map<Long, Float> collectToMap(List<Employee> list) {
        return list.stream()
                .collect(Collectors.toMap(
                                Employee::id,
                                Employee::salary
                        )
                );
    }
}
