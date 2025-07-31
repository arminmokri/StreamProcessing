package java_streams.advanced_transformations.top_n_elements;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    public List<Integer> topNElements(List<Integer> list, Integer n) {
        return list.stream()
                .sorted(Comparator.reverseOrder())
                .limit(n)
                .collect(Collectors.toList());
    }
}
