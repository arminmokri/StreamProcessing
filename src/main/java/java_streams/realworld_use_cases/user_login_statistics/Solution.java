package java_streams.realworld_use_cases.user_login_statistics;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
