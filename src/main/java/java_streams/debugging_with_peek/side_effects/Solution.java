package java_streams.debugging_with_peek.side_effects;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    public List<Integer> sideEffects(List<Integer> list) {
        List<Integer> sideEffectList = new ArrayList<>();
        list.stream()
                .peek(i -> sideEffectList.add(i * 2)) // side effect!
                .collect(Collectors.toList());
        return sideEffectList;
    }
}
