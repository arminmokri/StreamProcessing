package java_streams.primitive_streams.boxing_unboxing;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {

    // IntS(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) ->
    // [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    public List<Integer> boxingUnboxing(IntStream intStream) {
        return intStream
                .boxed()
                .collect(Collectors.toList());
    }
}
