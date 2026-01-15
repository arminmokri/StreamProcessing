package java_streams.custom_collectors.collector_to_summary_object;


import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    // [10, 20, 30, 40, 50] ->
    // "Count: 5\nSum: 150\nAverage: 30.0\nMin: 10\nMax: 50\n"
    public void collectorToSummaryObject(List<Integer> list) {
        IntSummaryStatistics stats =
                list.stream()
                        .collect(Collectors.summarizingInt(Integer::intValue));

        System.out.println("Count: " + stats.getCount());
        System.out.println("Sum: " + stats.getSum());
        System.out.println("Average: " + stats.getAverage());
        System.out.println("Min: " + stats.getMin());
        System.out.println("Max: " + stats.getMax());
    }
}
