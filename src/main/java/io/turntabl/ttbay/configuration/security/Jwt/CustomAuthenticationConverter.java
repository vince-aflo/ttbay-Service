package io.turntabl.ttbay.configuration.security.Jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

public class CustomAuthenticationConverter implements Converter<Jwt, JwtAuthenticationToken> {

    @Override
    public JwtAuthenticationToken convert(Jwt sourceToken) {
        String email = (String) sourceToken.getClaims().get("email");

        if(email == null) throw new InvalidBearerTokenException("Invalid Bearer Token");

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("USER");
        return new JwtAuthenticationToken(sourceToken, List.of(authority));

    }
}