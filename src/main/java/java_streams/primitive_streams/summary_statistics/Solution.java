package java_streams.primitive_streams.summary_statistics;


import java.util.IntSummaryStatistics;
import java.util.List;

public class Solution {

    record Statistic(Long Count, Long sum, Integer min, Integer max, Double avg) {
    }

    // [1, 2, 3, 4, 5, 6, 7, 8, 9, 10] ->
    // S(10L, 55L, 1, 10, 5.5d)
    public Statistic summaryStatistics(List<Integer> list) {
        IntSummaryStatistics statistics = list.stream()
                .mapToInt(Integer::intValue)
                .summaryStatistics();

        return new Statistic(
                statistics.getCount(),
                statistics.getSum(),
                statistics.getMin(),
                statistics.getMax(),
                statistics.getAverage()
        );
    }
}
