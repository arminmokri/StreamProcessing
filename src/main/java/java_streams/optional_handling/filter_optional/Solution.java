package java_streams.optional_handling.filter_optional;


import java.util.Optional;


public class Solution {

    // O(10), 15 -> O()
    // O(20), 15 -> O(20)
    public Optional<Integer> filterOptional(Optional<Integer> optional, Integer biggerThan) {
        return optional.filter(i -> i > biggerThan);

    }
}
