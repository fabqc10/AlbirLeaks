package com.fabdev.AlbirLeaks.users;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserService {
    private List<User> users = new ArrayList<>();

    public Optional<User> findByGoogleId (String googleId){
        return users.stream()
                .filter(user -> user.getGoogleId().equals(googleId))
                .findFirst();
    }

    public User createUser(String googleId, String email, String username){
        User newUser = new User(UUID.randomUUID().toString(),username,email,"USER",googleId, new ArrayList<>());
        users.add(newUser);
        System.out.println("NEW USER" + newUser.getGoogleId().toString());
        System.out.println("NEW USER" + newUser.getEmail().toString());
        System.out.println("NEW USER" + newUser.getUsername().toString());
        System.out.println(getUsers().size());

        return newUser;
    }

    public List<User> getUsers(){
        return users;
    }
}
