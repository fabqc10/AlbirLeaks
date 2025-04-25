package com.fabdev.AlbirLeaks.users;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserService {
    private final UserRepository userRepository;
//    private List<User> users = new ArrayList<>();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    public Optional<User> findByGoogleId(String googleId) {
//        return users.stream()
//                .filter(user -> user.getGoogleId().equals(googleId))
//                .findFirst();
//    }

    public Optional<User> findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }

//    public User createUser(String googleId, String email, String username) {
//        // Ensure the role matches the expected format in your security config
//        User newUser = new User(username, email,"USER", googleId, new ArrayList<>());
//        users.add(newUser);
//        System.out.println("USER CREATED");
//        return newUser;
//    }
public User createUser(String googleId, String email, String username, String imageUrl) {
    // Ensure the role matches the expected format in your security config
    User newUser = new User(username, email,"USER", googleId, new ArrayList<>(),imageUrl);
    System.out.println("USER CREATED");
    return userRepository.save(newUser);
}

//    public List<User> getUsers() {
//        return users;
//    }
public List<User> getUsers() {
    return userRepository.findAll();
}

}
