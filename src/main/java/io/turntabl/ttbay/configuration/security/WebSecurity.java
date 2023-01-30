package io.turntabl.ttbay.configuration.security;


import io.turntabl.ttbay.configuration.ApplicationConfig;
import io.turntabl.ttbay.configuration.Jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity  {

    public final ApplicationConfig applicationConfig;
    public final JwtAuthFilter jwtAuthFilter;
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
            http
                    .csrf()
                    .and()
                    .cors()
                    .disable()
                    .authorizeHttpRequests()
                    .requestMatchers("/api/v1/auth/authenticate").authenticated().and().oauth2Login();

            return http.build();
        }

    }
