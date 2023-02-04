package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.AuthResponse;
import io.turntabl.ttbay.enums.Role;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;


import java.util.Map;

@Service
public class UserAuthImpl implements UserAuthService {
    @Autowired
    UserRepository userRepository;

    @Override
    public AuthResponse register(Authentication authentication){
        JwtAuthenticationToken auth = (JwtAuthenticationToken) authentication;
        Map<String, Object> claims = auth.getTokenAttributes();
        String email = (String) claims.get("email");

        String name = (String) claims.get("name");
        String profilePhoto = (String) claims.get("picture");
        Role role = Role.USER; //TODO we are going to get this role from the claims when we add an admin role
                               //TODO (CustomAuthenticationConverter) customise the token before it gets here
        User user = User.builder().fullName(name).profileUrl(profilePhoto).email(email).role(role).build();

        boolean alreadyExists = userRepository.findByEmail(email).isPresent();

        if(!alreadyExists) {
            userRepository.save(user);

            return   AuthResponse.builder()
                    .message("Registered Successfully")
                    .email(email)
                    .fullName(name)
                    .picture(profilePhoto)
                    .hasFilledUserProfile(false)
                    .build();
        }
        //TODO check if the other fields apart from name, email and picture are empty
        // then in your newUserResponse we set hasFilledUserProfile to true in this else block

        return AuthResponse.builder()
                .message("Already registered")
                .email(email)
                .fullName(name)
                .picture(profilePhoto)
                .hasFilledUserProfile(true)
                .build();
    }

    public User findByEmail(String email) {
        var user = userRepository.findByEmail(email);
        return user.orElse(null);
    }
}