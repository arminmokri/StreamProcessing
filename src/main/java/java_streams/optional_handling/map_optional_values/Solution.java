package java_streams.optional_handling.map_optional_values;


import java.util.Optional;


public class Solution {


    // O("john") -> O("JOHN")
    public Optional<String> mapOptionalValues(Optional<String> name) {
        return name.map(String::toUpperCase);

    }
}
