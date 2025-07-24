package java_streams.mapping_and_flatmapping.flatmap_optional;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public List<String> flatmapNestedLists(List<Optional<String>> list) {
        return list.stream()
                .flatMap(Optional::stream)
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
        assertEquals(List.of("A", "B"),
                solution.flatmapNestedLists(List.of(
                        Optional.of("A"),
                        Optional.empty(),
                        Optional.of("B")

                )));
    }

    @Test
    public void testAllEmptyOptionals() {
        assertEquals(
                List.of(),
                solution.flatmapNestedLists(List.of(
                        Optional.empty(),
                        Optional.empty()
                ))
        );
    }

    @Test
    public void testAllPresentOptionals() {
        assertEquals(
                List.of("X", "Y", "Z"),
                solution.flatmapNestedLists(List.of(
                        Optional.of("X"),
                        Optional.of("Y"),
                        Optional.of("Z")
                ))
        );
    }

    @Test
    public void testEmptyInputList() {
        assertEquals(List.of(), solution.flatmapNestedLists(List.of()));
    }

    @Test
    public void testMixedCaseStrings() {
        assertEquals(
                List.of("hello", "WORLD", "Java"),
                solution.flatmapNestedLists(List.of(
                        Optional.of("hello"),
                        Optional.empty(),
                        Optional.of("WORLD"),
                        Optional.of("Java")
                ))
        );
    }

    @Test
    public void testOptionalWithEmptyString() {
        assertEquals(
                List.of("", "NotEmpty"),
                solution.flatmapNestedLists(List.of(
                        Optional.of(""),
                        Optional.empty(),
                        Optional.of("NotEmpty")
                ))
        );
    }
}
