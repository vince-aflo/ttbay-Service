package io.turntabl.ttbay.controller;

import io.turntabl.ttbay.service.UsernameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/profile/username")
public class UsernameController {
    private final UsernameService usernameService;

    @GetMapping("/{username}")
    public ResponseEntity<String> changeUsername(JwtAuthenticationToken auth, @PathVariable("username") String username){
       return ResponseEntity.ok(usernameService.updateUsername(auth,username));
    }
}
