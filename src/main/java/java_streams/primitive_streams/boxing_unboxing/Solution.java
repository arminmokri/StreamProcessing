package java_streams.primitive_streams.boxing_unboxing;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {

    public List<Integer> boxingUnboxing(IntStream intStream) {
        return intStream
                .boxed()
                .collect(Collectors.toList());
    }
}
