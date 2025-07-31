package java_streams.optional_handling.map_optional_values;


import java.util.Optional;


public class Solution {


    public Optional<String> mapOptionalValues(Optional<String> name) {
        return name.map(String::toUpperCase);

    }
}
