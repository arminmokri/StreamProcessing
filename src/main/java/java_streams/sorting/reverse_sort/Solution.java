package java_streams.sorting.reverse_sort;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    // [1, 4, 2, 5] -> [5, 4, 2, 1]
    public List<Integer> reverseSort(List<Integer> list) {
        return list.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }
}
