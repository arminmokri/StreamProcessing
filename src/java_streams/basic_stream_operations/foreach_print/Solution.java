package java_streams.basic_stream_operations.foreach_print;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public void foreachPrint(List<String> list) {
        list.stream()
                .forEach(System.out::println);
    }
}


class SolutionTest {
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
        String output = captureOutput(() -> solution.foreachPrint(List.of("apple", "apricot", "banana", "grape")));
        assertEquals("apple\napricot\nbanana\ngrape\n", output);
    }

    @Test
    public void testEmptyList() {
        String output = captureOutput(() -> solution.foreachPrint(List.of()));
        assertEquals("", output);
    }

    @Test
    public void testSingleElement() {
        String output = captureOutput(() -> solution.foreachPrint(List.of("kiwi")));
        assertEquals("kiwi\n", output);
    }


}
