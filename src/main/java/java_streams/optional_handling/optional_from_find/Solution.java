package java_streams.optional_handling.optional_from_find;


import java.util.List;
import java.util.Optional;


public class Solution {


    public Optional<Integer> optionalFromFind(List<Integer> list) {
        return list.stream()
                .filter(i -> i % 2 == 0)
                .findFirst();

    }
}
