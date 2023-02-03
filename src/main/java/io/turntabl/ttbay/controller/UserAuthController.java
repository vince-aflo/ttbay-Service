package io.turntabl.ttbay.controller;

import io.turntabl.ttbay.dto.AuthResponse;
import io.turntabl.ttbay.exceptions.UserAlreadyExistException;
import io.turntabl.ttbay.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;


@RestController
@RequestMapping("api/v1")
@CrossOrigin(origins = "*")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @GetMapping("/register")
    public ResponseEntity<AuthResponse>  authenticate(Authentication authentication) throws ParseException, UserAlreadyExistException {
        return ResponseEntity.status(HttpStatus.OK).body(userAuthService.register(authentication));
    }
}

