package io.turntabl.ttbay.service;

import io.turntabl.ttbay.dto.RegistrationResponse;
import io.turntabl.ttbay.exceptions.UserAlreadyExistException;
import io.turntabl.ttbay.model.User;
import org.springframework.security.core.Authentication;


import java.text.ParseException;

public interface UserRegisterService {
    RegistrationResponse register(Authentication authentication) throws ParseException, UserAlreadyExistException;
    User findByEmail(String email);
}