package java_streams.mapping_and_flatmapping.map_to_lengths;


import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    // ["one", "three", "seven"] -> [3, 5, 5]
    public List<Integer> mapToLengths(List<String> list) {
        return list.stream()
                .map(String::length)
                .collect(Collectors.toList());
    }
}
