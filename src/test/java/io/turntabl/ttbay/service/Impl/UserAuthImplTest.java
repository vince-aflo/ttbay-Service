package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.AuthResponse;
import io.turntabl.ttbay.enums.Role;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
class UserAuthImplTest{
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private UserAuthImpl serviceUnderTest;
    private User user;
    private JwtAuthenticationToken jwtAuthenticationToken;

    @BeforeEach
    void setUp(){
        serviceUnderTest = new UserAuthImpl(userRepository);
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
        Role role = Role.valueOf((jwtAuthenticationToken.getAuthorities().toArray())[0].toString());
        user = User.builder().fullName(given_name + family_name).profileUrl(picture).email(email).role(role).build();
    }

    @Test
    void register_existingUserEmail_doesNotSaveDetails(){
        doReturn(Optional.of(user)).when(userRepository).findByEmail(user.getEmail());
        AuthResponse response = serviceUnderTest.register(jwtAuthenticationToken);
        verify(userRepository,never()).save(any());
        Assertions.assertEquals("test@gmail.com", response.email());
    }

    @Test
    void register_freshUser_shouldReturnHasFilledUserProfileToFalseAndSaveDetails(){
        AuthResponse expectedResponse =  AuthResponse.builder().hasFilledUserProfile(false).build();
        AuthResponse actualResponse = serviceUnderTest.register(jwtAuthenticationToken);
        verify(userRepository,times(1)).save(any());
        Assertions.assertEquals(expectedResponse.hasFilledUserProfile(), actualResponse.hasFilledUserProfile());
    }

    @Test
    void register_existingUserWithoutUsername_shouldReturnHasFilledUserProfileToFalse(){
        doReturn(Optional.of(user)).when(userRepository).findByEmail(any());
        AuthResponse expectedResponse = AuthResponse.builder().hasFilledUserProfile(false).build();
        AuthResponse actualResponse = serviceUnderTest.register(jwtAuthenticationToken);
        verify(userRepository,never()).save(any());
        Assertions.assertEquals(expectedResponse.hasFilledUserProfile(), actualResponse.hasFilledUserProfile());
    }

    @Test
    void register_existingUserWithUsername_shouldReturnHasFilledUserProfileToTrue(){
        user.setUsername("emmanuel");
        doReturn(Optional.of(user)).when(userRepository).findByEmail(any());
        AuthResponse expectedResponse =  AuthResponse.builder().hasFilledUserProfile(true).build();
        AuthResponse actualResponse = serviceUnderTest.register(jwtAuthenticationToken);
        Assertions.assertEquals(expectedResponse.hasFilledUserProfile(), actualResponse.hasFilledUserProfile());
    }

}