package java_streams.realworld_use_cases.filter_active_users;


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Solution {

    record User(String name, Boolean active) {

    }

    public List<User> filterActiveUsers(List<User> list) {
        return list.stream()
                .filter(user -> Objects.nonNull(user) && Objects.nonNull(user.active) && user.active)
                .collect(Collectors.toList());
    }
}
