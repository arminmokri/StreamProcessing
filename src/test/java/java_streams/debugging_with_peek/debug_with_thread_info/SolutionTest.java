package java_streams.debugging_with_peek.debug_with_thread_info;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class SolutionTest {
    private static Solution solution;

    @BeforeAll
    public static void setUp() {
        solution = new Solution();
    }

    private String captureOutput(Runnable task) {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        try {
            task.run();
        } finally {
            System.setOut(System.out);
        }
        return outContent.toString();
    }

    @Test
    public void testDefaultCase() {
        List<Integer> input = List.of(10, 20, 30, 40, 50);

        String output = captureOutput(() -> solution.debugWithThreadInfo(input));

        // Basic content presence checks
        for (int number : input) {
            assertTrue(output.contains("peeked: " + number), "Missing peeked: " + number);
            assertTrue(output.contains("processed: " + (number * 2)), "Missing processed: " + (number * 2));
        }

        // Optional: check that thread info is present
        assertTrue(output.contains("Thread:"), "Missing thread information in output");
    }

    @Test
    public void testEmptyInput() {
        List<Integer> input = List.of();

        String output = captureOutput(() -> solution.debugWithThreadInfo(input));

        assertTrue(output.isEmpty(), "Expected no output for empty input");
    }

    @Test
    public void testSingleElement() {
        List<Integer> input = List.of(42);

        String output = captureOutput(() -> solution.debugWithThreadInfo(input));

        assertTrue(output.contains("peeked: 42"), "Missing peeked log for 42");
        assertTrue(output.contains("processed: 84"), "Missing processed log for 84");
        assertTrue(output.contains("Thread:"), "Missing thread information in output");
    }

    @Test
    public void testThreadDiversity() {
        List<Integer> input = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        String output = captureOutput(() -> solution.debugWithThreadInfo(input));

        long distinctThreadCount = output.lines()
                .filter(line -> line.startsWith("Thread:"))
                .map(line -> line.split("→")[0].trim()) // extract thread name
                .distinct()
                .count();

        // We expect at least 2 threads in most JVMs
        assertTrue(distinctThreadCount >= 2, "Expected multiple threads, got: " + distinctThreadCount);
    }
}
