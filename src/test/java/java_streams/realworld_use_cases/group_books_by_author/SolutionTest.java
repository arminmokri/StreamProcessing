package java_streams.realworld_use_cases.group_books_by_author;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SolutionTest {
    private static Solution solution;

    @BeforeAll
    public static void setUp() {
        solution = new Solution();
    }

    @Test
    public void testDefaultCase() {
        Map<String, List<String>> expected = Map.of(
                "Joshua Bloch", List.of("Effective Java"),
                "Robert Martin", List.of("Clean Code", "Clean Architecture"),
                "Brian Goetz", List.of("Java Concurrency")
        );
        Map<String, List<String>> actual = solution.groupBooksByAuthor(
                List.of(
                        new Solution.Book("Effective Java", "Joshua Bloch"),
                        new Solution.Book("Clean Code", "Robert Martin"),
                        new Solution.Book("Java Concurrency", "Brian Goetz"),
                        new Solution.Book("Clean Architecture", "Robert Martin")
                )
        );
        assertEquals(expected, actual);
    }

    @Test
    public void testEmptyList() {
        Map<String, List<String>> expected = Map.of();
        Map<String, List<String>> actual = solution.groupBooksByAuthor(List.of());
        assertEquals(expected, actual);
    }

    @Test
    public void testSingleAuthor() {
        Map<String, List<String>> expected = Map.of("Martin Fowler", List.of("Refactoring", "Patterns of Enterprise Application Architecture"));
        Map<String, List<String>> actual = solution.groupBooksByAuthor(
                List.of(
                        new Solution.Book("Refactoring", "Martin Fowler"),
                        new Solution.Book("Patterns of Enterprise Application Architecture", "Martin Fowler")
                )
        );
        assertEquals(expected, actual);
    }


}
