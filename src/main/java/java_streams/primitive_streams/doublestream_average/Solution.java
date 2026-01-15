package java_streams.primitive_streams.doublestream_average;


import java.util.List;

public class Solution {

    // [2.5, 3.5, 4.0] -> 3.33D
    public Double doubleStreamAverage(List<Double> list) {
        return list
                .stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);
    }
}
