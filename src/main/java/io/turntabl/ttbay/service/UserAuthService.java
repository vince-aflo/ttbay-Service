package io.turntabl.ttbay.service;

import io.turntabl.ttbay.dto.AuthResponse;
import org.springframework.security.core.Authentication;


import java.text.ParseException;

public interface UserAuthService{
    AuthResponse register(Authentication authentication) throws ParseException;
}