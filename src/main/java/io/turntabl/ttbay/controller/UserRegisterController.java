package io.turntabl.ttbay.controller;

import io.turntabl.ttbay.dto.RegistrationResponse;
import io.turntabl.ttbay.exceptions.UserAlreadyExistException;
import io.turntabl.ttbay.service.UserRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;


@RestController
@RequestMapping("api/v1")
public class UserRegisterController {

    @Autowired
    private UserRegisterService userRegisterService;

    @GetMapping("/register")
    public RegistrationResponse register(Authentication authentication) throws ParseException, UserAlreadyExistException {
        return userRegisterService.register(authentication);
    }
}

