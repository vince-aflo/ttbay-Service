package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.configuration.Jwt.JwtService;
import io.turntabl.ttbay.dto.AuthenticationResponse;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.model.enums.Role;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


import java.util.Map;

@Service
public class UserAuthImpl implements UserAuthService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtService jwtService;

    @Override
    public AuthenticationResponse authenticate(OAuth2User principle) {
        Map<String, Object> response = principle.getAttributes();

        String  existingUserEmail = (String) response.get("email");
        boolean alreadyExists = userRepository
                .findByEmail(existingUserEmail).isPresent();


        if(alreadyExists) {
            var user = userRepository.findByEmail(existingUserEmail)
                    .orElseThrow();
            var token = jwtService.generateToken(user);

            // TODO instead of generating token with jwt, lets get it from google

            return AuthenticationResponse.builder()
                    .message("ALready authenticated")
                    .token(token)
                    .build();
        }
        var user = User.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("picture"))
                .role(Role.valueOf("USER")).build();

        userRepository.save(user);
        var token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .message("Successfully Authenticated")
                .token(token)
                .build();

    }
}