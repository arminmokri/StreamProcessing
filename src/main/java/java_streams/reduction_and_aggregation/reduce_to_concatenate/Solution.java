package java_streams.reduction_and_aggregation.reduce_to_concatenate;


import java.util.List;

public class Solution {

    // ["a", "b", "c"] -> "abc"
    public String reduceToConcatenate(List<String> list) {
        return list.stream()
                .reduce((s1, s2) -> s1 + s2)
                .orElse("");
    }
}
