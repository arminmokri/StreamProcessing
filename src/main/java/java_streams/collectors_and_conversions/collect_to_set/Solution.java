package java_streams.collectors_and_conversions.collect_to_set;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Solution {


    public Set<String> collectToSet(List<String> list) {
        return list.stream()
                .collect(Collectors.toSet());
    }
}
