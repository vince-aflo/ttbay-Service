package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.AuthResponse;
import io.turntabl.ttbay.enums.Role;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
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

@SpringBootTest
class UserAuthImplTest {

    @Autowired
    private UserRepository userRepository;
    @MockBean
    private UserAuthImpl serviceUnderTest;
    private User user;

    private JwtAuthenticationToken jwtAuthenticationToken;

    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
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

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testThat_UserIsRetrievedFromTheDb_UsingHisEmail() {
        User expectedUser = userRepository.save(user);
        User retrievedUserByEmail = userRepository.findByEmail(user.getEmail()).orElse(null);
        assert retrievedUserByEmail != null;
        Assertions.assertEquals(retrievedUserByEmail.getEmail(), expectedUser.getEmail());
    }

    @Test
    void testThat_WhenFreshUserRegisters_ReturnHasFilledUerProfileToFalse() {
        authResponse = AuthResponse.builder().hasFilledUserProfile(false).build();
        AuthResponse expectedResponse = authResponse;

        AuthResponse actualResponse = serviceUnderTest.register(jwtAuthenticationToken);

        Assertions.assertEquals(expectedResponse.isHasFilledUserProfile(), actualResponse.isHasFilledUserProfile());

    }

    @Test
    void testThat_WhenAnExistingUserWithoutUsernameRegisters_ReturnHasFilledUerProfileToFalse() {
        userRepository.save(user);

        authResponse = AuthResponse.builder().hasFilledUserProfile(false).build();

        AuthResponse expectedResponse = authResponse;
        AuthResponse actualResponse = serviceUnderTest.register(jwtAuthenticationToken);
        Assertions.assertEquals(expectedResponse.isHasFilledUserProfile(), actualResponse.isHasFilledUserProfile());

    }

    @Test
    void testThat_WhenAnExistingUserWithUsernameRegisters_ReturnHasFilledUerProfileToTrue() {
        user.setUsername("emmanuel");
        userRepository.save(user);

        authResponse = AuthResponse.builder().hasFilledUserProfile(true).build();

        AuthResponse expectedResponse = authResponse;
        AuthResponse actualResponse = serviceUnderTest.register(jwtAuthenticationToken);
        Assertions.assertEquals(expectedResponse.isHasFilledUserProfile(), actualResponse.isHasFilledUserProfile());

    }

    @Test
    void testThat_FindByEmail_ShouldReturnNUllWhenEmailIsNotFoundInDb() {
        var retrievedUserByEmail = userRepository.findByEmail("albert@gmail.com").orElse(null);
        Assertions.assertNull(retrievedUserByEmail);
    }


}