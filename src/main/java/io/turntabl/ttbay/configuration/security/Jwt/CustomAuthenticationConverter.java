package io.turntabl.ttbay.configuration.security.Jwt;

import io.turntabl.ttbay.enums.Role;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.UserAuthService;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class CustomAuthenticationConverter implements Converter<Jwt, JwtAuthenticationToken> {
    private final UserRepository userRepository;

    @Override
    public JwtAuthenticationToken convert(Jwt source) {
        String email = (String) source.getClaims().get("email");

        if (email == null) {
            throw new InvalidBearerTokenException("Invalid bearer token");
        }
        User user = userRepository.findByEmail(email).orElse(null);
        SimpleGrantedAuthority authority;
        if (user != null) {
            authority = new SimpleGrantedAuthority(user.getRole().toString());
            return new JwtAuthenticationToken(source, List.of(authority));
        } else authority = new SimpleGrantedAuthority(Role.USER.toString());

        return new JwtAuthenticationToken(source, List.of(authority));
    }
}
