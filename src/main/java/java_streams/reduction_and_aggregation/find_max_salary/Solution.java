package java_streams.reduction_and_aggregation.find_max_salary;

import java.util.List;

public class Solution {

    public Integer findMaxSalary(List<Integer> list) {
        return list.stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }
}
