package java_streams.basic_stream_operations.find_first_match;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class SolutionTest {
    private static Solution solution;

    @BeforeAll
    public static void setUp() {
        solution = new Solution();
    }

    @Test
    public void testDefaultCase() {
        assertEquals("apple", solution.findFirstMatch(List.of("apple", "apricot", "banana", "grape"), "a"));
    }

    @Test
    public void testNoMatch() {
        assertNull(null, solution.findFirstMatch(List.of("banana", "grape", "melon"), "x"));
    }

    @Test
    public void testEmptyList() {
        assertNull(null, solution.findFirstMatch(List.of(), "a"));
    }

    @Test
    public void testSingleExactMatch() {
        assertEquals("banana", solution.findFirstMatch(List.of("banana"), "b"));
    }

    @Test
    public void testMultipleMatchesReturnsFirst() {
        assertEquals("apple", solution.findFirstMatch(List.of("apple", "apricot", "avocado"), "a"));
    }

    @Test
    public void testCaseSensitivity() {
        assertNull(null, solution.findFirstMatch(List.of("Apple", "Apricot"), "a")); // lowercase 'a' vs capital 'A'
    }

    @Test
    public void testPrefixIsEntireString() {
        assertEquals("grape", solution.findFirstMatch(List.of("apple", "banana", "grape"), "grape"));
    }


}
