package java_streams.exception_handling.recover_from_exceptions;


import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    // ["10", "abc", "20", "xyz", "30"] ->
    // [10, -1, 20, -1, 30]
    public List<Integer> recoverFromExceptions(List<String> list) {
        return list.stream()
                .map(s -> {
                            try {
                                return Integer.parseInt(s);
                            } catch (NumberFormatException e) {
                                System.err.println("item '" + s + "' is not int.");
                                return -1;
                            }
                        }
                )
                .collect(Collectors.toList());
    }
}
