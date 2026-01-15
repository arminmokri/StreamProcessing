package java_streams.advanced_transformations.sliding_window_simulation;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {

    // [1, 2, 3, 4, 5], 3 -> [[1, 2, 3], [2, 3, 4], [3, 4, 5]]
    public List<List<Integer>> slidingWindowSimulation(List<Integer> list, Integer n) {
        return IntStream
                .range(0, list.size() - n + 1)
                .mapToObj(i -> list.subList(i, i + n))
                .collect(Collectors.toList());
    }
}
