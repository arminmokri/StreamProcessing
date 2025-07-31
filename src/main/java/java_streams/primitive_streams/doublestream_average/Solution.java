package java_streams.primitive_streams.doublestream_average;


import java.util.List;

public class Solution {


    public Double doubleStreamAverage(List<Double> list) {
        return list
                .stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);
    }
}
