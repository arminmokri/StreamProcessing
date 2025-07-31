package java_streams.debugging_with_peek.log_in_pipeline;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
        List<String> input = List.of("apple", "banana", "cherry");

        // Capture output and result together
        final List<Integer>[] result = new List[1];
        String output = captureOutput(() ->
                result[0] = solution.logInPipeline(input, 5)
        );

        assertEquals(
                "Original: apple\n" +
                        "Mapped to length: 5\n" +
                        "Original: banana\n" +
                        "Mapped to length: 6\n" +
                        "Filtered length > 5: 6\n" +
                        "Original: cherry\n" +
                        "Mapped to length: 6\n" +
                        "Filtered length > 5: 6\n",
                output
        );

        assertEquals(List.of(6, 6), result[0]);
    }

    @Test
    public void testAllShortStrings() {
        List<String> input = List.of("a", "bb", "cat");

        final List<Integer>[] result = new List[1];
        String output = captureOutput(() ->
                result[0] = solution.logInPipeline(input, 5)
        );

        assertEquals(
                "Original: a\n" +
                        "Mapped to length: 1\n" +
                        "Original: bb\n" +
                        "Mapped to length: 2\n" +
                        "Original: cat\n" +
                        "Mapped to length: 3\n",
                output
        );

        assertEquals(List.of(), result[0]);
    }

    @Test
    public void testEmptyInputList() {
        List<String> input = List.of();

        final List<Integer>[] result = new List[1];
        String output = captureOutput(() ->
                result[0] = solution.logInPipeline(input, 5)
        );

        assertEquals("", output);
        assertEquals(List.of(), result[0]);
    }

    @Test
    public void testMixedLengthStrings() {
        List<String> input = List.of("hi", "hello", "streaming", "java");

        final List<Integer>[] result = new List[1];
        String output = captureOutput(() ->
                result[0] = solution.logInPipeline(input, 5)
        );

        assertEquals(
                "Original: hi\n" +
                        "Mapped to length: 2\n" +
                        "Original: hello\n" +
                        "Mapped to length: 5\n" +
                        "Original: streaming\n" +
                        "Mapped to length: 9\n" +
                        "Filtered length > 5: 9\n" +
                        "Original: java\n" +
                        "Mapped to length: 4\n",
                output
        );

        assertEquals(List.of(9), result[0]);
    }

}
