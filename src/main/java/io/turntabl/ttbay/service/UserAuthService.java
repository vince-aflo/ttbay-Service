package io.turntabl.ttbay.service;

import io.turntabl.ttbay.dto.AuthenticationResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;


public interface UserAuthService {
    AuthenticationResponse authenticate(OAuth2User principle);
}
