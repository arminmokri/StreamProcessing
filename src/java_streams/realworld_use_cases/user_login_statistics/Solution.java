package java_streams.realworld_use_cases.user_login_statistics;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    record Login(String username, String timestamp) {

    }

    public Map<String, Long> userLoginStatistics(List<Login> list) {
        return list.stream()
                .collect(
                        Collectors.groupingBy(
                                Login::username,
                                Collectors.counting()
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
        Map<String, Long> expected = Map.of(
                "alice", 3L,
                "bob", 2L
        );

        Map<String, Long> actual = solution.userLoginStatistics(
                List.of(
                        new Solution.Login("alice", "2025-07-30T08:00"),
                        new Solution.Login("bob", "2025-07-30T08:15"),
                        new Solution.Login("alice", "2025-07-30T09:00"),
                        new Solution.Login("alice", "2025-07-30T10:00"),
                        new Solution.Login("bob", "2025-07-30T11:00")
                )
        );

        assertEquals(expected, actual);
    }

}
