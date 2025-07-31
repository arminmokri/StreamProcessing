package java_streams.custom_collectors.collector_with_downstream;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    record Item(String category, Integer price) {
    }

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
