package java_streams.collectors_and_conversions.group_by_field;


import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    // [E(1, "John", 4000, "software"), E(2, "Jane", 5000, "network"),
    // E(3, "Andy", 6000, "software"), E(4, "Micheal", 7000, "network")]
    // {"software", [E(1, "John", 4000, "software"), E(3, "Andy", 6000, "software")],
    // "network", [E(2, "Jane", 5000, "network"), E(4, "Micheal", 7000, "network")]}
    public Map<String, List<Employee>> groupByField(List<Employee> list) {
        return list.stream()
                .collect(Collectors.groupingBy(Employee::department));
    }
}
