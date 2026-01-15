package java_streams.sorting.sort_custom_objects;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    // ["apple", "fig", "banana"] -> ["fig", "apple", "banana"]
    public List<String> sortCustomObjects(List<String> list) {
        return list.stream()
                .sorted(Comparator.comparing(String::length))
                .collect(Collectors.toList());
    }

}
