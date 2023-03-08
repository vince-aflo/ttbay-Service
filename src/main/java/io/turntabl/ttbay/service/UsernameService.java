package io.turntabl.ttbay.service;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;

public interface UsernameService {
    String updateUsername(JwtAuthenticationToken auth,String username);
    Map<String, String> findAllUnavailableUsernames();
    Map<String, String> removeActiveUsersUsername(JwtAuthenticationToken auth, Map<String,String> usernames);
}
