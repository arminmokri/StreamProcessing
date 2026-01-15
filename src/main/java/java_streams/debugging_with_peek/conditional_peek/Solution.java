package java_streams.debugging_with_peek.conditional_peek;


import java.util.List;

public class Solution {

    // [10, 20, 30, 5, 40], 15, 10 ->
    // "Peek >15: 20\nPeek >15: 20\n"
    // "Peek >15: 30\nFinal Output: 30\n"
    // "Peek >15: 40\nFinal Output: 40\n"
    public void peekIntermediateValues(List<Integer> list, Integer peekSize, Integer filterSize) {
        list.stream()
                .peek(n -> {
                            if (n > peekSize) {
                                System.out.println("Peek >15: " + n);
                            }
                        }
                )
                .filter(n -> n > filterSize)
                .forEach(n -> System.out.println("Final Output: " + n));
    }
}
