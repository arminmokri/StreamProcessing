package java_streams.reduction_and_aggregation.average_salary;


import java.util.List;

public class Solution {

    public Double averageSalary(List<Integer> list) {
        return list.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }
}
