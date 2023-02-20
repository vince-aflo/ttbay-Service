package io.turntabl.ttbay.configuration.security;

import io.turntabl.ttbay.configuration.security.Jwt.CustomAuthenticationConverter;
import io.turntabl.ttbay.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity  {

    private final UserAuthService userAuthService ;
    @Value("${jwt-set-url}")
    private String jwtSetUrl;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 ->oauth2.jwt( jwt -> jwt.jwkSetUri(jwtSetUrl)
                        .jwtAuthenticationConverter(new CustomAuthenticationConverter(userAuthService))));

        return http.build();
    }

}
