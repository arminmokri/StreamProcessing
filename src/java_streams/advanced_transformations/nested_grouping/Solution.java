package java_streams.advanced_transformations.nested_grouping;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    record Student(String name, String department, Integer year) {

    }

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


class SolutionTest {
    private static Solution solution;

    @BeforeAll
    public static void setUp() {
        solution = new Solution();
    }

    @Test
    public void testDefaultCase() {
        assertEquals(
                Map.of("CS", Map.of(2023, List.of("Alice", "Bob")), "Math", Map.of(2022, List.of("Eve")))
                , solution.nestedGrouping(
                        List.of(
                                new Solution.Student("Alice", "CS", 2023),
                                new Solution.Student("Bob", "CS", 2023),
                                new Solution.Student("Eve", "Math", 2022)

                        )
                )
        );
    }


}
