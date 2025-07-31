package java_streams.optional_handling.filter_optional;


import java.util.Optional;


public class Solution {

    public Optional<Integer> filterOptional(Optional<Integer> optional, Integer biggerThan) {
        return optional.filter(i -> i > biggerThan);

    }
}
