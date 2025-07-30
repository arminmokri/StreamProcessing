package java_streams.realworld_use_cases.sum_orders_per_customer;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

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


class SolutionTest {
    private static Solution solution;

    @BeforeAll
    public static void setUp() {
        solution = new Solution();
    }

    @Test
    public void testDefaultCase() {
        Map<String, Double> actual = solution.sumOrdersPerCustomer(
                List.of(
                        new Solution.Order("Alice", 120.0),
                        new Solution.Order("Bob", 90.5),
                        new Solution.Order("Alice", 130.0),
                        new Solution.Order("Bob", 50.0),
                        new Solution.Order("Charlie", 200.0)
                )
        );

        assertEquals(250.0, actual.get("Alice"), 0.001);
        assertEquals(140.5, actual.get("Bob"), 0.001);
        assertEquals(200.0, actual.get("Charlie"), 0.001);
    }

    @Test
    public void testEmptyList() {
        Map<String, Double> result = solution.sumOrdersPerCustomer(List.of());
        assertEquals(0, result.size());
    }

    @Test
    public void testNegativeAmounts() {
        Map<String, Double> result = solution.sumOrdersPerCustomer(
                List.of(
                        new Solution.Order("Dave", -50.0),
                        new Solution.Order("Dave", 25.0)
                )
        );
        assertEquals(-25.0, result.get("Dave"), 0.001);
    }


}
