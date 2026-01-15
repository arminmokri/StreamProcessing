package java_streams.primitive_streams.intstream_range;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {

    // 1, 10 -> [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    public List<Integer> intStreamRange(Integer from, Integer to) {
        return IntStream
                .rangeClosed(from, to)
                .boxed()
                .collect(Collectors.toList());
    }
}
