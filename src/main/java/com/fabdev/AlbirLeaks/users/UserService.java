package com.fabdev.AlbirLeaks.users;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserService {
    private List<User> users = new ArrayList<>();

    public Optional<User> findByGoogleId(String googleId) {
        return users.stream()
                .filter(user -> user.getGoogleId().equals(googleId))
                .findFirst();
    }

    public User createUser(String googleId, String email, String username) {
        // Ensure the role matches the expected format in your security config
        User newUser = new User(UUID.randomUUID().toString(), username, email,"USER", googleId, new ArrayList<>());
        users.add(newUser);
        System.out.println("USER CREATED");
        return newUser;
    }

    public List<User> getUsers() {
        return users;
    }
}
