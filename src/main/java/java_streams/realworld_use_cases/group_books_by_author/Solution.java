package java_streams.realworld_use_cases.group_books_by_author;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    record Book(String title, String author) {

    }

    //  [B("Effective Java", "Joshua Bloch"),
    //  B("Clean Code", "Robert Martin"),
    //  B("Java Concurrency", "Brian Goetz"),
    //  B("Clean Architecture", "Robert Martin")] ->
    // {"Joshua Bloch", ["Effective Java")],
    // "Robert Martin", ["Clean Code", "Clean Architecture"],
    // "Brian Goetz", ["Java Concurrency"]}
    public Map<String, List<String>> groupBooksByAuthor(List<Book> list) {
        return list.stream()
                .collect(Collectors.groupingBy(
                                Book::author,
                                Collectors.mapping(Book::title, Collectors.toList())
                        )
                );
    }
}
