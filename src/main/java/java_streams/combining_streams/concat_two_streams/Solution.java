package java_streams.combining_streams.concat_two_streams;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solution {

    public List<Integer> concatTwoStreams(List<Integer> list1, List<Integer> list2) {
        return Stream
                .concat(list1.stream(), list2.stream())
                .collect(Collectors.toList());
    }
}
