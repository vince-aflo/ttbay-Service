package io.turntabl.ttbay.controller;

import io.turntabl.ttbay.dto.AuthResponse;
import io.turntabl.ttbay.exceptions.UserAlreadyExistException;
import io.turntabl.ttbay.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;


@RestController
@RequestMapping("api/v1")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @GetMapping("/register")
    public AuthResponse authenticate(Authentication authentication) throws ParseException, UserAlreadyExistException {
        return userAuthService.register(authentication);
    }
}

