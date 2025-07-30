package java_streams.custom_collectors.custom_collector_for_string_join;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collector;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Solution {

    public String customCollectorForStringJoin(List<String> list) {
        return list.stream()
                .collect(joiningWithComma());
    }

    public Collector<String, StringJoiner, String> joiningWithComma() {
        return Collector.of(
                () -> new StringJoiner(","),    // supplier
                StringJoiner::add,                      // accumulator
                StringJoiner::merge,                    // combiner
                StringJoiner::toString                  // finisher
        );
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
        assertEquals("apple,banana,cherry", solution.customCollectorForStringJoin(List.of("apple", "banana", "cherry")));
    }


}

