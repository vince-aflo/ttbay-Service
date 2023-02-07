package io.turntabl.ttbay.configuration.security;

import io.turntabl.ttbay.configuration.security.Jwt.CustomAuthenticationConverter;
import io.turntabl.ttbay.service.Impl.UserAuthImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class WebSecurity  {
    @Autowired
    private UserAuthImpl userAuth;
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
                        .jwtAuthenticationConverter(new CustomAuthenticationConverter(userAuth))));

        return http.build();
    }

}
