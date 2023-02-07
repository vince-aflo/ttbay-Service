package io.turntabl.ttbay.configuration.security.Jwt;

import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.service.Impl.UserAuthImpl;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
@AllArgsConstructor
public class CustomAuthenticationConverter implements Converter<Jwt, JwtAuthenticationToken> {
    private final UserAuthImpl userRegister;

    @Override
    public JwtAuthenticationToken convert(Jwt sourceToken) {
        String email = (String) sourceToken.getClaims().get("email");

        if(email == null) throw new InvalidBearerTokenException("Invalid Bearer Token");

        User user = userRegister.findByEmail(email);

        if(user != null) {
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString());
            return new JwtAuthenticationToken(sourceToken, List.of(authority));
        }
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("USER");
        return new JwtAuthenticationToken(sourceToken, List.of(authority));
    }
}