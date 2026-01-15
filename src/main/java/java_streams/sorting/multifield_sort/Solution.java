package java_streams.sorting.multifield_sort;


import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Solution {

    record Person(String name, int age) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return age == person.age && Objects.equals(name, person.name);
        }

    }

    // [P("John", 22), P("Alice", 28),
    // P("John", 20), P("Alice", 19)] ->
    // [P("Alice", 19), P("Alice", 28),
    // P("John", 20), P("John", 22)]
    public List<Person> multiFieldSort(List<Person> list) {
        return list.stream()
                .sorted(Comparator.comparing(Person::name).thenComparing(Person::age))
                .collect(Collectors.toList());
    }
}
