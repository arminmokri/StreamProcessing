package java_streams.custom_collectors.collector_with_downstream;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    record Item(String category, Integer price) {
    }

    // [I("A", 10), I("A", 20), I("B", 5), I("B", 15), I("C", 7)] ->
    // {"A", 30, "B", 20, "C", 7}
    public Map<String, Integer> collectorWithDownstream(List<Item> list) {
        return list.stream()
                .collect(
                        Collectors.groupingBy(
                                Item::category,
                                Collectors.summingInt(Item::price)
                        )
                );
    }
}
