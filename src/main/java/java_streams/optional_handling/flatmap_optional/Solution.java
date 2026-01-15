package java_streams.optional_handling.flatmap_optional;


import java.util.Optional;


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

    // O(U(O(A(O(C("Berlin")))))) -> O("Berlin")
    public Optional<String> flatMapOptional(Optional<User> optional) {
        return optional
                .flatMap(User::getAddress)
                .flatMap(Address::getCity)
                .map(City::name);
    }
}
