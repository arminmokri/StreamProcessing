package java_streams.debugging_with_peek.debug_with_thread_info;


import java.util.List;

public class Solution {

    public void debugWithThreadInfo(List<Integer> list) {
        list.parallelStream()
                .peek(n -> System.out.printf("Thread: %s → peeked: %d%n", Thread.currentThread().getName(), n))
                .map(i -> i * 2)
                .forEach(n -> System.out.printf("Thread: %s → processed: %d%n", Thread.currentThread().getName(), n));
    }
}
