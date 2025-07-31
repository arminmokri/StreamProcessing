package java_streams.mapping_and_flatmapping.map_to_uppercase;

import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    public List<String> mapToUppercase(List<String> list) {
        return list.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }
}
