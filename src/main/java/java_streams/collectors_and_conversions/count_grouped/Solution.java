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

    public Map<Float, Long> groupByField(List<Student> list) {
        return list.stream()
                .collect(Collectors.groupingBy(Student::grade, Collectors.counting()));
    }
}
