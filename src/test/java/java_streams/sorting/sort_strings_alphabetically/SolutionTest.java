package java_streams.sorting.sort_strings_alphabetically;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SolutionTest {
    private static Solution solution;

    @BeforeAll
    public static void setUp() {
        solution = new Solution();
    }

    @Test
    public void testDefaultCase() {
        assertEquals(List.of("apple", "banana", "cherry"), solution.sortStringsAlphabetically(List.of("banana", "apple", "cherry")));
    }

    @Test
    public void testEmptyList() {
        assertEquals(List.of(), solution.sortStringsAlphabetically(List.of()));
    }

    @Test
    public void testSingleElement() {
        assertEquals(List.of("orange"), solution.sortStringsAlphabetically(List.of("orange")));
    }

    @Test
    public void testWithDuplicates() {
        assertEquals(List.of("apple", "apple", "banana"), solution.sortStringsAlphabetically(List.of("banana", "apple", "apple")));
    }

    @Test
    public void testCaseSensitivity() {
        // Default sort is case-sensitive: uppercase letters come before lowercase
        assertEquals(List.of("Apple", "banana", "cherry"), solution.sortStringsAlphabetically(List.of("banana", "Apple", "cherry")));
    }

    @Test
    public void testSpecialCharacters() {
        assertEquals(List.of("#hashtag", "apple", "banana"), solution.sortStringsAlphabetically(List.of("banana", "#hashtag", "apple")));
    }

    @Test
    public void testAlreadySorted() {
        assertEquals(List.of("apple", "banana", "cherry"), solution.sortStringsAlphabetically(List.of("apple", "banana", "cherry")));
    }

    @Test
    public void testReverseOrder() {
        assertEquals(List.of("apple", "banana", "cherry"), solution.sortStringsAlphabetically(List.of("cherry", "banana", "apple")));
    }
}
