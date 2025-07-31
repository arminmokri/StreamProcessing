package java_streams.custom_collectors.collector_to_summary_object;


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
        String output = captureOutput(() -> solution.collectorToSummaryObject(List.of(10, 20, 30, 40, 50)));
        assertEquals(
                "Count: 5\n" +
                        "Sum: 150\n" +
                        "Average: 30.0\n" +
                        "Min: 10\n" +
                        "Max: 50\n",
                output
        );
    }

    @Test
    public void testEmptyList() {
        String output = captureOutput(() -> solution.collectorToSummaryObject(List.of()));
        String newline = System.lineSeparator();

        assertEquals(
                "Count: 0" + newline +
                        "Sum: 0" + newline +
                        "Average: 0.0" + newline +
                        "Min: 2147483647" + newline +
                        "Max: -2147483648" + newline,
                output
        );
    }

    @Test
    public void testNegativeNumbers() {
        String output = captureOutput(() -> solution.collectorToSummaryObject(List.of(-5, -10, -1)));
        String newline = System.lineSeparator();

        assertEquals(
                "Count: 3" + newline +
                        "Sum: -16" + newline +
                        "Average: -5.333333333333333" + newline +
                        "Min: -10" + newline +
                        "Max: -1" + newline,
                output
        );
    }


}

