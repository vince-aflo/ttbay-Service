package io.turntabl.ttbay.service;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;

public interface UsernameService {
    String updateUsername(JwtAuthenticationToken auth,String username);
    Map<String, String> findAllUsernamesWithEmails();
    Map<String, String> removeActiveUsersUsernameAndEmail(JwtAuthenticationToken auth, Map<String,String> usernames);
}
