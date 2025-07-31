package java_streams.sorting.sort_by_salary;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    public List<Integer> sortBySalary(List<Integer> list) {
        return list.stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }
}
