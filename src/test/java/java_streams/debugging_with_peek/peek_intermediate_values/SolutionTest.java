package java_streams.debugging_with_peek.peek_intermediate_values;


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
        List<Integer> input = List.of(1, 2, 3, 4, 5);

        // Capture output and result together
        final List<Integer>[] result = new List[1];
        String output = captureOutput(() ->
                result[0] = solution.peekIntermediateValues(input)
        );

        assertEquals(
                "Filtered: 2\n" +
                        "Mapped: 20\n" +
                        "Filtered: 4\n" +
                        "Mapped: 40\n",
                output
        );

        assertEquals(List.of(20, 40), result[0]);
    }

    @Test
    public void testAllOddNumbers() {
        List<Integer> input = List.of(1, 3, 5, 7);

        final List<Integer>[] result = new List[1];
        String output = captureOutput(() ->
                result[0] = solution.peekIntermediateValues(input)
        );

        assertEquals("", output); // No even numbers = no output
        assertEquals(List.of(), result[0]); // No mapped values
    }

    @Test
    public void testEmptyList() {
        List<Integer> input = List.of();

        final List<Integer>[] result = new List[1];
        String output = captureOutput(() ->
                result[0] = solution.peekIntermediateValues(input)
        );

        assertEquals("", output); // No input = no output
        assertEquals(List.of(), result[0]);
    }
}
