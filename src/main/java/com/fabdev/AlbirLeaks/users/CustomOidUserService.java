package com.fabdev.AlbirLeaks.users;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidUserService extends OidcUserService {
    private UserService userService;

    public CustomOidUserService(UserService userService) {
        this.userService = userService;
    }

    public OidcUser loadUser(OidcUserRequest userRequest){
        // Load the user information from the OIDC provider
        OidcUser oidcUser = super.loadUser(userRequest);

        // Extract necessary user information from the OIDC user
        String googleId = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String username = oidcUser.getGivenName();

        // Find or create the user in the local database
        userService
                .findByGoogleId(googleId)
                .orElseGet(
                        ()-> userService
                                .createUser(googleId,email,username));
        // Return the loaded OIDC user
        System.out.println("CREATED");
        return oidcUser;

    }


}
