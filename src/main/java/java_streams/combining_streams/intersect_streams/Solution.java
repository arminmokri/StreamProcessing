package java_streams.combining_streams.intersect_streams;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Solution {

    // [1, 2, 3], [2, 3, 4] -> [2, 3]
    public List<Integer> intersectStreams(List<Integer> list1, List<Integer> list2) {

        Set<Integer> set2 = Set.copyOf(list2);

        return list1.stream()
                .filter(set2::contains)
                .collect(Collectors.toList());
    }
}
