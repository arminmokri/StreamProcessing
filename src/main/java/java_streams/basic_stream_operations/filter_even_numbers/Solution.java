package java_streams.basic_stream_operations.filter_even_numbers;

import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    // [1, 4, 5, 10] -> [4, 10]
    public List<Integer> filterEvenNumbers(List<Integer> list) {
        return list.stream()
                .filter(i -> i % 2 == 0)
                .collect(Collectors.toList());
    }
}
