package java_streams.primitive_streams.longstream_generate;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Solution {

    public List<Long> longStreamGenerate(Integer count, Integer startingAt) {
        return LongStream
                .range(startingAt, startingAt + count)
                .boxed()
                .collect(Collectors.toList());
    }
}
