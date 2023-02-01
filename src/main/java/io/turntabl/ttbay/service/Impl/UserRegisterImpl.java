package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.RegistrationResponse;
import io.turntabl.ttbay.exceptions.UserAlreadyExistException;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.model.enums.Role;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;


import java.text.ParseException;
import java.util.Map;

@Service
public class UserRegisterImpl implements UserRegisterService {
    @Autowired
    UserRepository userRepository;

    @Override
    public RegistrationResponse register(Authentication authentication) throws ParseException, UserAlreadyExistException {
        JwtAuthenticationToken auth = (JwtAuthenticationToken) authentication;
        Map<String, Object> claims = auth.getTokenAttributes();
        String email = (String) claims.get("email");

        boolean alreadyExists = userRepository.findByEmail(email).isPresent();

        if(alreadyExists) throw new UserAlreadyExistException("A user with "+email+ "already exist");

        String name = (String) claims.get("name");
        String profilePhoto = (String) claims.get("picture");
        Role role = Role.valueOf("USER");
        User user = User.builder().fullName(name).email(email).role(role).build();

        userRepository.save(user);

        return RegistrationResponse.builder()
                .message("Registered Successfully")
                .email(email)
                .fullName(name)
                .picture(profilePhoto)
                .build();
    }

    public User findByEmail(String email) {
        var user = userRepository.findByEmail(email);
        if(user.isPresent()){
            return user.get();
        }
        return null;
    }
}