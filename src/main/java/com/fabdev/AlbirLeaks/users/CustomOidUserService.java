package com.fabdev.AlbirLeaks.users;

import com.fabdev.AlbirLeaks.jobs.JobsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomOidUserService extends OidcUserService {
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(JobsController.class);

    public CustomOidUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // Load the user information from the OIDC provider
        OidcUser oidcUser = super.loadUser(userRequest);

        List<SimpleGrantedAuthority> mappedAuthorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        logger.info("Assigning roles: {}", mappedAuthorities);

        // Log the ID Token
        String idToken = userRequest.getIdToken().getTokenValue();
        System.out.println("ID Token: " + idToken);

        // Log the Access Token
        String accessToken = userRequest.getAccessToken().getTokenValue();
        System.out.println("Access Token: " + accessToken);

        // Extract necessary user information from the OIDC user
        String googleId = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String username = oidcUser.getGivenName();

        // Find or create the user in the local database
        User user = userService
                .findByGoogleId(googleId)
                .orElseGet(() -> userService.createUser(googleId, email, username));

        // Additional log to confirm user creation logic
        System.out.println("User with Google ID " + googleId + " is created or already exists.");
        System.out.println("User Roles: " + user.getRole());

        // Return the loaded OIDC user
        return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());

    }
}
