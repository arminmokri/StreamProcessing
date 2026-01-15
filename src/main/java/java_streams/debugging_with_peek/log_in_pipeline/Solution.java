package java_streams.debugging_with_peek.log_in_pipeline;


import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    // ["apple", "banana", "cherry"] ->
    // "Original: apple\nMapped to length: 5\n"
    // "Original: banana\nMapped to length: 6\nFiltered length > 5: 6\n"
    // "Original: cherry\nMapped to length: 6\nFiltered length > 5: 6\n"
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
