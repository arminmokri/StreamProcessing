package java_streams.advanced_transformations.nested_grouping;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    record Student(String name, String department, Integer year) {

    }

    // [S("Alice", "CS", 2023), S("Bob", "CS", 2023), S("Eve", "Math", 2022)] ->
    // {"CS", {2023, ["Alice", "Bob"]}, "Math", {2022, ["Eve"]}}
    public Map<String, Map<Integer, List<String>>> nestedGrouping(List<Student> list) {
        return list.stream()
                .collect(Collectors.groupingBy(
                                Student::department,
                                Collectors.groupingBy(
                                        Student::year,
                                        Collectors.mapping(Student::name, Collectors.toList())
                                )
                        )
                );


    }
}
