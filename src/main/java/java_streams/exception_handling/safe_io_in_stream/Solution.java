package java_streams.exception_handling.safe_io_in_stream;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Solution {

    // ["file1.txt", "file2.txt"] ->
    // "Files (file1.txt) in Java might be tricky, but it is fun enough!\n"
    // "Files (file2.txt) in Java might be tricky, but it is fun enough!\n"
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
