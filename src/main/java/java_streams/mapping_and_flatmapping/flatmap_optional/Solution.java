package java_streams.mapping_and_flatmapping.flatmap_optional;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Solution {

    public List<String> flatmapNestedLists(List<Optional<String>> list) {
        return list.stream()
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

    }
}
