package java_streams.realworld_use_cases.user_login_statistics;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    record Login(String username, String timestamp) {

    }

    // [L("alice", "2025-07-30T08:00"), L("bob", "2025-07-30T08:15"),
    // L("alice", "2025-07-30T09:00"), L("alice", "2025-07-30T10:00")
    // L("bob", "2025-07-30T11:00")] ->
    // {"alice", 3L, "bob", 2L}
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
