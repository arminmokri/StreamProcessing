package java_streams.map_stream_operations.stream_over_map_entries;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        String output = captureOutput(() -> solution.streamOverMapEntries(Map.of("a", 1, "b", 2)));
        Set<String> lines = Set.of(output.split("\\R")); // handles \n or \r\n
        assertTrue(lines.contains("a=1"));
        assertTrue(lines.contains("b=2"));
        assertEquals(2, lines.size());
    }

    @Test
    public void testEmptyMap() {
        String output = captureOutput(() -> solution.streamOverMapEntries(Map.of()));
        assertEquals("", output);
    }

    @Test
    public void testSingleEntry() {
        String output = captureOutput(() -> solution.streamOverMapEntries(Map.of("x", 42)));
        assertEquals("x=42\n", output);
    }

    @Test
    public void testNumericKeys() {
        String output = captureOutput(() -> solution.streamOverMapEntries(Map.of("1", 100, "2", 200)));
        Set<String> lines = Set.of(output.split("\\R"));
        assertTrue(lines.contains("1=100"));
        assertTrue(lines.contains("2=200"));
    }

    @Test
    public void testSpecialCharacters() {
        String output = captureOutput(() -> solution.streamOverMapEntries(Map.of("key$", 5, "value@", 10)));
        Set<String> lines = Set.of(output.split("\\R"));
        assertTrue(lines.contains("key$=5"));
        assertTrue(lines.contains("value@=10"));
    }
}
