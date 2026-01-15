package java_streams.realworld_use_cases.top_selling_products;


import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    record Product(String name, Integer unitsSold) {

    }

    // [P("Laptop", 50), P("Smartphone", 120),
    // P("Headphones", 120), P("Tablet", 80)] ->
    // [P("Smartphone", 120), P("Headphones", 120)]
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
