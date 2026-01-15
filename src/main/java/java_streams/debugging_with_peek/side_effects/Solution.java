package java_streams.debugging_with_peek.side_effects;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    // [1, 2, 3] -> [2, 4, 6]
    public List<Integer> sideEffects(List<Integer> list) {
        List<Integer> sideEffectList = new ArrayList<>();
        list.stream()
                .peek(i -> sideEffectList.add(i * 2)) // side effect!
                .collect(Collectors.toList());
        return sideEffectList;
    }
}
