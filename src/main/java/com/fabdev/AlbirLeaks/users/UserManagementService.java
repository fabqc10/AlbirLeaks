package com.fabdev.AlbirLeaks.users;

import org.springframework.stereotype.Service;

@Service
public class UserManagementService {
    private final UserService userService;

    public UserManagementService(UserService userService) {
        this.userService = userService;
    }

    public synchronized User findOrCreateUser(String googleId, String email, String username) {
        return userService.findByGoogleId(googleId)
                .orElseGet(() -> userService.createUser(googleId, email, username));
    }
}
