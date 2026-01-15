package java_streams.realworld_use_cases.sum_orders_per_customer;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    record Order(String customer, Double amount) {

    }

    // [O("Alice", 120.0), O("Bob", 90.5),
    // O("Alice", 130.0), O("Bob", 50.0),
    // O("Charlie", 200.0)] ->
    // {Alice", 250.0, "Bob", 140.5, "Charlie", 200.0}
    public Map<String, Double> sumOrdersPerCustomer(List<Order> list) {
        return list.stream()
                .collect(
                        Collectors.groupingBy(
                                Order::customer,
                                Collectors.summingDouble(Order::amount)
                        )
                );
    }
}
