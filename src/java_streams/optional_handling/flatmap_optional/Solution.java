package java_streams.optional_handling.flatmap_optional;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class Solution {


    record City(String name) {
    }

    record Address(City city) {
        public Optional<City> getCity() {
            return Optional.ofNullable(city);
        }
    }

    record User(Address address) {
        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }
    }

    public Optional<String> flatMapOptional(Optional<User> optional) {
        return optional
                .flatMap(User::getAddress)
                .flatMap(Address::getCity)
                .map(City::name);
    }
}


class SolutionTest {
    private static Solution solution;

    @BeforeAll
    public static void setUp() {
        solution = new Solution();
    }

    @Test
    public void testDefaultCase() {

        Solution.User user = new Solution.User(new Solution.Address(new Solution.City("Berlin")));

        assertEquals(
                Optional.of("Berlin"),
                solution.flatMapOptional(Optional.of(user))
        );
    }

    @Test
    public void testUserWithoutAddress() {
        Solution.User user = new Solution.User(null);
        assertEquals(
                Optional.empty(),
                solution.flatMapOptional(Optional.of(user))
        );
    }

    @Test
    public void testUserWithAddressButNoCity() {
        Solution.User user = new Solution.User(new Solution.Address(null));
        assertEquals(
                Optional.empty(),
                solution.flatMapOptional(Optional.of(user))
        );
    }

    @Test
    public void testEmptyOptionalUser() {
        assertEquals(
                Optional.empty(),
                solution.flatMapOptional(Optional.empty())
        );
    }

}
