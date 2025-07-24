package java_streams.sorting.multifield_sort;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    public List<Person> multiFieldSort(List<Person> list) {
        return list.stream()
                .sorted(Comparator.comparing(Person::name).thenComparing(Person::age))
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
                List.of(
                        new Solution.Person("Alice", 19),
                        new Solution.Person("Alice", 28),
                        new Solution.Person("John", 20),
                        new Solution.Person("John", 22)
                ), solution.multiFieldSort(
                        List.of(
                                new Solution.Person("John", 22),
                                new Solution.Person("Alice", 28),
                                new Solution.Person("John", 20),
                                new Solution.Person("Alice", 19)
                        )
                ));
    }

    @Test
    public void testEmptyList() {
        assertEquals(List.of(), solution.multiFieldSort(List.of()));
    }

    @Test
    public void testSingleElementList() {
        List<Solution.Person> input = List.of(new Solution.Person("Alice", 30));
        assertEquals(input, solution.multiFieldSort(input));
    }

    @Test
    public void testSameNameDifferentAge() {
        List<Solution.Person> input = List.of(
                new Solution.Person("Bob", 25),
                new Solution.Person("Bob", 20),
                new Solution.Person("Bob", 30)
        );
        List<Solution.Person> expected = List.of(
                new Solution.Person("Bob", 20),
                new Solution.Person("Bob", 25),
                new Solution.Person("Bob", 30)
        );
        assertEquals(expected, solution.multiFieldSort(input));
    }

    @Test
    public void testDifferentNames() {
        List<Solution.Person> input = List.of(
                new Solution.Person("Charlie", 25),
                new Solution.Person("Alice", 30),
                new Solution.Person("Bob", 20)
        );
        List<Solution.Person> expected = List.of(
                new Solution.Person("Alice", 30),
                new Solution.Person("Bob", 20),
                new Solution.Person("Charlie", 25)
        );
        assertEquals(expected, solution.multiFieldSort(input));
    }


}
