package java_streams.debugging_with_peek.peek_intermediate_values;


import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    // [1, 2, 3, 4, 5] ->
    // "Filtered: 2\nMapped: 20\n"
    // "Filtered: 4\nMapped: 40\n"
    public List<Integer> peekIntermediateValues(List<Integer> list) {
        return list.stream()
                .filter(n -> n % 2 == 0)
                .peek(n -> System.out.println("Filtered: " + n))
                .map(n -> n * 10)
                .peek(n -> System.out.println("Mapped: " + n))
                .collect(Collectors.toList());
    }
}
