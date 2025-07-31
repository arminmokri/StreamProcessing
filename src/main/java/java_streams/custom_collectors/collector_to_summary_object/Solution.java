package java_streams.custom_collectors.collector_to_summary_object;


import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

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
