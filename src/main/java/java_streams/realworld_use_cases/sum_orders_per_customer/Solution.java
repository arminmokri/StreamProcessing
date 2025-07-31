package java_streams.realworld_use_cases.sum_orders_per_customer;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    record Order(String customer, Double amount) {

    }

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
