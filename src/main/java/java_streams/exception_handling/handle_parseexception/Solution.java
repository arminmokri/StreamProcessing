package java_streams.exception_handling.handle_parseexception;


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Solution {

    public List<Integer> handleParseException(List<String> list) {
        return list.stream()
                .map(s -> {
                            try {
                                return Integer.parseInt(s);
                            } catch (NumberFormatException e) {
                                System.err.println("item '" + s + "' is not int.");
                                return null;
                            }
                        }
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}

