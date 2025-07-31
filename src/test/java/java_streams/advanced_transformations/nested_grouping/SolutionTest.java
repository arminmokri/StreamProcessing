package java_streams.advanced_transformations.nested_grouping;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SolutionTest {
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
