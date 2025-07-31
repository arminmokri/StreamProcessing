package java_streams.debugging_with_peek.conditional_peek;


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
        List<Integer> input = List.of(10, 20, 30, 5, 40);

        String output = captureOutput(() -> solution.peekIntermediateValues(input, 15, 10));

        assertEquals(
                "Peek >15: 20\n" +
                        "Final Output: 20\n" +
                        "Peek >15: 30\n" +
                        "Final Output: 30\n" +
                        "Peek >15: 40\n" +
                        "Final Output: 40\n",
                output
        );
    }

    @Test
    public void testNoPeekOrFilter() {
        List<Integer> input = List.of(1, 2, 3);

        String output = captureOutput(() -> solution.peekIntermediateValues(input, 15, 10));

        assertEquals("", output, "Expected no output when no values exceed thresholds");
    }

    @Test
    public void testOnlyPeeked() {
        List<Integer> input = List.of(16, 17); // > peekSize=15, but not > filterSize=20

        String output = captureOutput(() -> solution.peekIntermediateValues(input, 15, 20));

        assertEquals(
                "Peek >15: 16\n" +
                        "Peek >15: 17\n",
                output
        );
    }

    @Test
    public void testAllPeekedAndFiltered() {
        List<Integer> input = List.of(25, 30);

        String output = captureOutput(() -> solution.peekIntermediateValues(input, 15, 10));

        assertEquals(
                "Peek >15: 25\n" +
                        "Final Output: 25\n" +
                        "Peek >15: 30\n" +
                        "Final Output: 30\n",
                output
        );
    }

    @Test
    public void testOnlyFiltered() {
        List<Integer> input = List.of(11, 13); // > filterSize=10, but < peekSize=15

        String output = captureOutput(() -> solution.peekIntermediateValues(input, 15, 10));

        assertEquals(
                "Final Output: 11\n" +
                        "Final Output: 13\n",
                output
        );
    }

}
