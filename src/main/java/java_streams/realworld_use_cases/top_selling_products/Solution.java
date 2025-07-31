package java_streams.realworld_use_cases.top_selling_products;


import java.util.List;
import java.util.stream.Collectors;

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
