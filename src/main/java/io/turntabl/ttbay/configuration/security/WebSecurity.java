package io.turntabl.ttbay.configuration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@RequiredArgsConstructor
public class WebSecurity  {

    @Value("${jwt-set-url}")
    private String jwtSetUrl;


    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
         .oauth2ResourceServer(oauth2 ->oauth2.jwt( jwt -> jwt.jwkSetUri(jwtSetUrl)));


        return http.build();
    }

}
