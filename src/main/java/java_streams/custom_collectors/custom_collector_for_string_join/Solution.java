package java_streams.custom_collectors.custom_collector_for_string_join;


import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collector;

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
