package java_streams.mapping_and_flatmapping.flatmap_nested_lists;


import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    public List<Integer> flatmapNestedLists(List<List<Integer>> lists) {
        return lists.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
