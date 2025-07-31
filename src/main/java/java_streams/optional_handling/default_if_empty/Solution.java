package java_streams.optional_handling.default_if_empty;


import java.util.List;


public class Solution {


    public String defaultIfEmpty(List<String> list, String username) {
        return list.stream()
                .filter(username::equals)
                .findFirst()
                .orElse("No Match");

    }
}
