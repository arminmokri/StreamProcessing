package java_streams.debugging_with_peek.debug_with_thread_info;


import java.util.List;

public class Solution {

    // [10, 20, 30, 40, 50] ->
    // "Thread: 'some-name' → peeked: 10\nThread: 'some-name' → processed: 20\n"
    // "Thread: 'some-name' → peeked: 20\nThread: 'some-name' → processed: 40\n"
    // "Thread: 'some-name' → peeked: 30\nThread: 'some-name' → processed: 60\n"
    // "Thread: 'some-name' → peeked: 40\nThread: 'some-name' → processed: 80\n"
    // "Thread: 'some-name' → peeked: 50\nThread: 'some-name' → processed: 100\n"
    public void debugWithThreadInfo(List<Integer> list) {
        list.parallelStream()
                .peek(n -> System.out.printf("Thread: %s → peeked: %d%n", Thread.currentThread().getName(), n))
                .map(i -> i * 2)
                .forEach(n -> System.out.printf("Thread: %s → processed: %d%n", Thread.currentThread().getName(), n));
    }
}
