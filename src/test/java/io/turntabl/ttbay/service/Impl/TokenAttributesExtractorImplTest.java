package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.enums.Role;
import io.turntabl.ttbay.service.TokenAttributesExtractor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Map;


@SpringBootTest
class TokenAttributesExtractorImplTest {
    @Autowired
    private TokenAttributesExtractor tokenAttributesExtractor;
    private JwtAuthenticationToken jwtAuthenticationToken;

    @BeforeEach
    void setup(){
        String tokenValue = "token";
        String email = "test@gmail.com";
        String picture = "xxxxxx";
        String given_name = "Emma";
        String family_name = "tk";
        Instant issuedAt = Instant.now();
        Instant expiredAt = Instant.now().plusSeconds(100000);
        Map<String, Object> headers = Map.of("aud", "aud");
        Map<String, Object> claims = Map.of("email", email, "picture", picture, "given_name", given_name, "family_name", family_name);
        Jwt jwt = new Jwt(tokenValue, issuedAt, expiredAt, headers, claims);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(Role.USER.toString());
        jwtAuthenticationToken = new JwtAuthenticationToken(jwt, List.of(authority));
    }
    @Test
    void testThat_extractEmailFromToken_returnsTheEmailFromTheToken() {
        String expectedEmail = "test@gmail.com";
        String actualEmail = tokenAttributesExtractor.extractEmailFromToken(jwtAuthenticationToken);
        Assertions.assertEquals(expectedEmail, actualEmail);
    }

}