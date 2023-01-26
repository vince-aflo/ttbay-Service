package io.turntabl.ttbay.controller;

import io.turntabl.ttbay.dto.AuthenticationResponse;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.model.enums.Role;
import io.turntabl.ttbay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @Autowired
    private  UserRepository userRepository;


    @GetMapping("/authenticate")
    public AuthenticationResponse authenticateUser(@AuthenticationPrincipal OAuth2User principle){
      Map<String, Object> response = principle.getAttributes();

        boolean alreadyExists = userRepository
                .findByEmail((String) response.get("email"))
                .isPresent();

        if(alreadyExists)
            return AuthenticationResponse.builder()
                    .message("ALready authenticated")
                    .build();

      var user = User.builder()
              .name((String) response.get("given_name") +" "+ (String) response.get("family_name"))
              .email((String) response.get("email"))
              .picture((String) response.get("picture"))
              .role(Role.USER).build();

      userRepository.save(user);

      return AuthenticationResponse.builder()
              .message("Successfully Authenticated")
              .token("Set up jwt we token to generate token")
              .build();
    }
}
