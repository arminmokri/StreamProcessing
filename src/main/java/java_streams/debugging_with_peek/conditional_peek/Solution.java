package java_streams.debugging_with_peek.conditional_peek;


import java.util.List;

public class Solution {

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
