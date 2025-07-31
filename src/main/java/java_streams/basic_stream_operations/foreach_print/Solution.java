package java_streams.basic_stream_operations.foreach_print;

import java.util.List;

public class Solution {

    public void foreachPrint(List<String> list) {
        list.stream()
                .forEach(System.out::println);
    }
}
