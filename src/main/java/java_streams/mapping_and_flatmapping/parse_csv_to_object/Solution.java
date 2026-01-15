package java_streams.mapping_and_flatmapping.parse_csv_to_object;


import java.util.List;
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

    // ["1,John,5000", "2,Jane,6000"] ->
    // [E(1, "John", 5000), E(2, "Jane", 6000)]
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
