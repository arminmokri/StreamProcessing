package java_streams.combining_streams.zip_two_lists;


import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {

    // ["a", "b"], [1, 2] -> [a=1, b=2]
    public List<AbstractMap.SimpleEntry<String, Integer>> concatTwoStreams(List<String> list1, List<Integer> list2) {
        return IntStream
                .range(0, Math.min(list1.size(), list2.size()))
                .mapToObj(i -> new AbstractMap.SimpleEntry<>(list1.get(i), list2.get(i)))
                .collect(Collectors.toList());
    }
}
