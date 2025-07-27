package java_streams.exception_handling.recover_from_exceptions;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

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


class SolutionTest {
    private static Solution solution;

    @BeforeAll
    public static void setUp() {
        solution = new Solution();
    }

    @Test
    public void testDefaultCase() {
        assertEquals(
                List.of(10, -1, 20, -1, 30),
                solution.recoverFromExceptions(List.of("10", "abc", "20", "xyz", "30"))
        );
    }

    @Test
    public void testAllInvalid() {
        assertEquals(List.of(-1, -1, -1), solution.recoverFromExceptions(List.of("abc", "def", "xyz")));
    }

    @Test
    public void testAllValid() {
        assertEquals(List.of(1, 2, 3), solution.recoverFromExceptions(List.of("1", "2", "3")));
    }

    @Test
    public void testEmptyList() {
        assertEquals(List.of(), solution.recoverFromExceptions(List.of()));
    }

    @Test
    public void testNegativeAndZero() {
        assertEquals(List.of(0, -1, 100), solution.recoverFromExceptions(List.of("0", "-1", "100")));
    }

}
