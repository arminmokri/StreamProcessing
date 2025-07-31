package java_streams.primitive_streams.intstream_range;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {

    public List<Integer> intStreamRange(Integer from, Integer to) {
        return IntStream
                .rangeClosed(from, to)
                .boxed()
                .collect(Collectors.toList());
    }
}
