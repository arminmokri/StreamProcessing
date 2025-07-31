package java_streams.debugging_with_peek.log_in_pipeline;


import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    public List<Integer> logInPipeline(List<String> list, Integer filterSize) {
        return list.stream()
                .peek(s -> System.out.println("Original: " + s))
                .map(String::length)
                .peek(i -> System.out.println("Mapped to length: " + i))
                .filter(i -> i > filterSize)
                .peek(i -> System.out.println("Filtered length > 5: " + i))
                .collect(Collectors.toList());
    }
}
