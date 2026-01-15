package java_streams.sorting.sort_by_salary;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    // [5000, 2000, 7000] -> [2000, 5000, 7000]
    public List<Integer> sortBySalary(List<Integer> list) {
        return list.stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }
}
