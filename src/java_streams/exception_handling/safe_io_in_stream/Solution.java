package java_streams.exception_handling.safe_io_in_stream;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public void safeIOInStream(List<String> list) {
        list.stream()
                .flatMap(this::safeReadLines)
                .forEach(System.out::println);
    }

    private Stream<String> safeReadLines(String filePath) {
        try {
            return Files.lines(Path.of(filePath));
        } catch (IOException e) {
            System.err.println("Failed to read: " + filePath + " -> " + e.getMessage());
            return Stream.empty(); // safely continue
        }
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
    public void testDefaultCase(@TempDir Path tempDir) throws IOException {
        Path file1 = tempDir.resolve("file1.txt");
        Path file2 = tempDir.resolve("file2.txt");

        String line1 = "Files (file1.txt) in Java might be tricky, but it is fun enough!";
        String line2 = "Files (file2.txt) in Java might be tricky, but it is fun enough!";

        Files.writeString(file1, line1);
        Files.writeString(file2, line2);

        String output = captureOutput(() ->
                solution.safeIOInStream(List.of(file1.toString(), file2.toString()))
        );

        assertEquals(line1 + "\n" + line2 + "\n", output);
    }

    @Test
    public void testWithInvalidFile(@TempDir Path tempDir) {
        Path invalidFile = tempDir.resolve("missing.txt");

        String output = captureOutput(() ->
                solution.safeIOInStream(List.of(invalidFile.toString()))
        );

        assertEquals("", output); // no output to stdout
    }
}
