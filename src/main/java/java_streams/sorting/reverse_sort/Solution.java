package java_streams.sorting.reverse_sort;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    public List<Integer> reverseSort(List<Integer> list) {
        return list.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }
}
