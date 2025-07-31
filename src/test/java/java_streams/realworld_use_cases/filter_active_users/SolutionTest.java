package java_streams.realworld_use_cases.filter_active_users;


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
        List<Solution.User> expected = List.of(
                new Solution.User("Alice", Boolean.TRUE),
                new Solution.User("Charlie", Boolean.TRUE)

        );
        List<Solution.User> actual = solution.filterActiveUsers(
                List.of(
                        new Solution.User("Alice", Boolean.TRUE),
                        new Solution.User("Bob", Boolean.FALSE),
                        new Solution.User("Charlie", Boolean.TRUE),
                        new Solution.User("Diana", Boolean.FALSE)
                )
        );
        assertEquals(expected, actual);
    }

    @Test
    public void testWithNullCase() {
        List<Solution.User> expected = List.of(
                new Solution.User("Alice", Boolean.TRUE)

        );
        List<Solution.User> actual = solution.filterActiveUsers(
                List.of(
                        new Solution.User("Alice", Boolean.TRUE),
                        new Solution.User("Bob", null)
                )
        );
        assertEquals(expected, actual);
    }

    @Test
    public void testEmptyList() {
        assertEquals(List.of(), solution.filterActiveUsers(List.of()));
    }


}
