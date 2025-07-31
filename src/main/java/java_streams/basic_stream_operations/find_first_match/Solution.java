package java_streams.basic_stream_operations.find_first_match;

import java.util.List;

public class Solution {

    public String findFirstMatch(List<String> list, String startsWith) {
        return list.stream()
                .filter(s -> s.startsWith(startsWith))
                .findFirst()
                .orElse(null);
    }
}
