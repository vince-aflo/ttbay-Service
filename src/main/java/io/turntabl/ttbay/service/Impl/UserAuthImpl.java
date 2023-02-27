package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.AuthResponse;
import io.turntabl.ttbay.enums.Role;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.UserAuthService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class UserAuthImpl implements UserAuthService {
    @Autowired
    UserRepository userRepository;

    @Override
    public AuthResponse register(Authentication authentication) {
        JwtAuthenticationToken auth = (JwtAuthenticationToken) authentication;
        Map<String, Object> claims = auth.getTokenAttributes();
        String email = (String) claims.get("email");

        String name = (String) claims.get("name");
        String profilePhoto = (String) claims.get("picture");
        Role role = Role.valueOf((auth.getAuthorities().toArray())[0].toString());
        User user = User.builder().fullName(name).profileUrl(profilePhoto).email(email).role(role).build();

        boolean alreadyExists = userRepository.findByEmail(email).isPresent();

        if (!alreadyExists) {
            userRepository.save(user);

            return AuthResponse.builder()
                    .message("Registered Successfully")
                    .email(email)
                    .fullName(name)
                    .picture(profilePhoto)
                    .hasFilledUserProfile(false)
                    .build();
        }
        //TODO check if username is empty, that means the profile has not been filled

        return AuthResponse.builder()
                .message("Already registered")
                .email(email)
                .fullName(name)
                .picture(profilePhoto)
                .hasFilledUserProfile(true)
                .build();
    }

}