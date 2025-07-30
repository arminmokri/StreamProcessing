package java_streams.realworld_use_cases.filter_active_users;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    record User(String name, Boolean active) {

    }

    public List<User> filterActiveUsers(List<User> list) {
        return list.stream()
                .filter(user -> Objects.nonNull(user) && Objects.nonNull(user.active) && user.active)
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
