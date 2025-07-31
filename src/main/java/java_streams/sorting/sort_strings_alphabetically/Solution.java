package java_streams.sorting.sort_strings_alphabetically;


import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    public List<String> sortStringsAlphabetically(List<String> list) {
        return list.stream()
                .sorted()
                .collect(Collectors.toList());
    }
}
