package java_streams.collectors_and_conversions.count_grouped;


import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Solution {

    record Student(long id, String name, float grade) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Student student = (Student) o;
            return id == student.id

                    && Objects.equals(name, student.name)
                    && Float.compare(student.grade, grade) == 0;
        }

    }

    // [S(1, "John", 85f), S(2, "Jane", 76f),
    // S(3, "Andy", 85f), S(4, "Micheal", 76f)] ->
    // {85f, 2L, 76f, 2L}
    public Map<Float, Long> groupByField(List<Student> list) {
        return list.stream()
                .collect(Collectors.groupingBy(Student::grade, Collectors.counting()));
    }
}
