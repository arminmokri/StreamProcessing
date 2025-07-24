package java_streams.mapping_and_flatmapping.map_to_uppercase;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public List<String> mapToUppercase(List<String> list) {
        return list.stream()
                .map(String::toUpperCase)
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
                List.of("HELLO", "WORLD"),
                solution.mapToUppercase(List.of("hello", "world")));
    }

    @Test
    public void testEmptyList() {
        assertEquals(List.of(), solution.mapToUppercase(List.of()));
    }

    @Test
    public void testMixedCaseStrings() {
        assertEquals(
                List.of("JAVA", "STREAMS", "ARE", "FUN"),
                solution.mapToUppercase(List.of("Java", "Streams", "Are", "Fun"))
        );
    }

    @Test
    public void testAlreadyUppercase() {
        assertEquals(
                List.of("TEST", "DATA"),
                solution.mapToUppercase(List.of("TEST", "DATA"))
        );
    }

    @Test
    public void testStringsWithNumbersAndSymbols() {
        assertEquals(
                List.of("HELLO123!", "@UPPER#"),
                solution.mapToUppercase(List.of("hello123!", "@upper#"))
        );
    }

    @Test
    public void testWhitespaceOnlyStrings() {
        assertEquals(
                List.of("   ", "\t\n"),
                solution.mapToUppercase(List.of("   ", "\t\n"))
        );
    }

    @Test
    public void testEmptyStrings() {
        assertEquals(
                List.of("", "", ""),
                solution.mapToUppercase(List.of("", "", ""))
        );
    }
}
