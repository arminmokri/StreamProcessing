package java_streams.realworld_use_cases.top_selling_products;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    record Product(String name, Integer unitsSold) {

    }

    public List<Product> topSellingProducts(List<Product> list) {

        Integer max = list.stream()
                .mapToInt(Product::unitsSold)
                .max()
                .orElse(0);

        return list.stream()
                .filter(p -> p.unitsSold.equals(max))
                .collect(Collectors.toList());
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
        List<Solution.Product> expected = List.of(
                new Solution.Product("Smartphone", 120),
                new Solution.Product("Headphones", 120)
        );

        List<Solution.Product> actual = solution.topSellingProducts(
                List.of(
                        new Solution.Product("Laptop", 50),
                        new Solution.Product("Smartphone", 120),
                        new Solution.Product("Headphones", 120),
                        new Solution.Product("Tablet", 80)
                )
        );

        assertEquals(expected, actual);
    }

    @Test
    public void testEmptyList() {
        List<Solution.Product> actual = solution.topSellingProducts(List.of());
        assertEquals(List.of(), actual);
    }

    @Test
    public void testSingleTopProduct() {
        List<Solution.Product> expected = List.of(new Solution.Product("Phone", 150));
        List<Solution.Product> actual = solution.topSellingProducts(
                List.of(
                        new Solution.Product("Phone", 150),
                        new Solution.Product("TV", 80),
                        new Solution.Product("Camera", 100)
                )
        );
        assertEquals(expected, actual);
    }
}
