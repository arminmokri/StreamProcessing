package java_streams.mapping_and_flatmapping.map_to_lengths;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public List<Integer> mapToLengths(List<String> list) {
        return list.stream()
                .map(String::length)
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
        assertEquals(List.of(3, 5, 5), solution.mapToLengths(List.of("one", "three", "seven")));
    }

    @Test
    public void testEmptyList() {
        assertEquals(List.of(), solution.mapToLengths(List.of()));
    }

    @Test
    public void testSameLengthStrings() {
        assertEquals(List.of(4, 4, 4), solution.mapToLengths(List.of("code", "java", "test")));
    }

    @Test
    public void testEmptyStrings() {
        assertEquals(List.of(0, 1, 0), solution.mapToLengths(List.of("", "a", "")));
    }

    @Test
    public void testWhitespaceInStrings() {
        assertEquals(List.of(4, 6, 7), solution.mapToLengths(List.of(" hi ", "hello ", " good  ")));
    }
}
