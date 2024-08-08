//package com.fabdev.AlbirLeaks.security;
//
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.http.ResponseEntity;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.json.jackson2.JacksonFactory;
//
//import java.util.Collections;
//
//@RestController
//public class AuthController {
//
//    private final GoogleIdTokenVerifier verifier;
//
//    public AuthController() throws Exception {
//        this.verifier = new GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance())
//                .setAudience(Collections.singletonList("YOUR_GOOGLE_CLIENT_ID"))
//                .build();
//    }
//
//    @PostMapping("/auth/login")
//    public ResponseEntity<?> loginWithGoogle(@RequestHeader("Authorization") String authorizationHeader) {
//        String googleIdToken = authorizationHeader.replace("Bearer ", "");
//
//        try {
//            GoogleIdToken idToken = verifier.verify(googleIdToken);
//            if (idToken != null) {
//                GoogleIdToken.Payload payload = idToken.getPayload();
//
//                // You can access user information from payload
//                String userId = payload.getSubject();  // Use this for user identification in your system
//                String email = payload.getEmail();
//                boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
//                String name = (String) payload.get("name");
//
//                // Perform any additional logic here (e.g., creating or updating user records)
//
//                return ResponseEntity.ok().body("Authenticated successfully");
//            } else {
//                return ResponseEntity.status(401).body("Invalid ID token");
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(401).body("Token verification failed");
//        }
//    }
//}
