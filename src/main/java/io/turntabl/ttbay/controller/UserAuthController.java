package io.turntabl.ttbay.controller;

import io.turntabl.ttbay.dto.AuthenticationResponse;
import io.turntabl.ttbay.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/v1/auth")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @GetMapping("/authenticate")
    public AuthenticationResponse authenticateUser(@AuthenticationPrincipal OAuth2User principle) {
        return userAuthService.authenticate(principle);
    }
}
