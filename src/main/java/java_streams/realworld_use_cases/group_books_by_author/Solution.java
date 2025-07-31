package java_streams.realworld_use_cases.group_books_by_author;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    record Book(String title, String author) {

    }

    public Map<String, List<String>> groupBooksByAuthor(List<Book> list) {
        return list.stream()
                .collect(Collectors.groupingBy(
                                Book::author,
                                Collectors.mapping(Book::title, Collectors.toList())
                        )
                );
    }
}
