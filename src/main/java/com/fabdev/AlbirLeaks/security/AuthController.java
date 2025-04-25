package com.fabdev.AlbirLeaks.security;

import com.fabdev.AlbirLeaks.users.User;
import com.fabdev.AlbirLeaks.users.UserManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final JwtDecoder jwtDecoder;
    private final UserManagementService userManagementService;

    public AuthController(JwtDecoder jwtDecoder, UserManagementService userManagementService) {
        this.jwtDecoder = jwtDecoder;
        this.userManagementService = userManagementService;
    }

    @PostMapping("/api/auth/google")
    public ResponseEntity<?> authenticateUser(@RequestHeader("Authorization") String token) {
        // Extract the JWT token from the Authorization header
        System.out.println("TOKEN FROM FRONTEND: " + token);
        String jwtToken = token.replace("Bearer ", "");

        // Decode and validate the JWT token
        Jwt decodedJwt = jwtDecoder.decode(jwtToken);

        // Extract user info from decoded token (e.g., email, name, sub)
        String googleId = decodedJwt.getClaimAsString("sub");
        String email = decodedJwt.getClaimAsString("email");
        String username = decodedJwt.getClaimAsString("name");
        String imageUrl = decodedJwt.getClaimAsString("picture");

        // Check if user exists in the database, if not create a new user
        // Example: userService.findOrCreateUser(userId, email, name);
        User existingUser = userManagementService.findOrCreateUser(googleId, email, username,imageUrl);


        return ResponseEntity.ok("User authenticated successfully AND TOKEN FROM FRONTEND TO BACK: " + token);
    }
}
