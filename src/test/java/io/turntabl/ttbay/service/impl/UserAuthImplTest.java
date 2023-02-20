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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
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
    private Jwt jwt;

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
        Map<String, Object> claims = Map.of("email", email, "picture", picture, "given_name",
                given_name, "family_name", family_name);

        jwt = new Jwt(tokenValue, issuedAt, expiredAt, headers, claims);

        user = User.builder().fullName(given_name + family_name).profileUrl(picture).email(email).role(Role.USER).build();

    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testThat_UserIsRetrievedFromTheDb_UsingHisEmail() {
        User expectedUser = userRepository.save(user);
        System.out.println(expectedUser.getEmail());
        User retrievedUserByEmail = serviceUnderTest.findByEmail(user.getEmail());
        System.out.println(retrievedUserByEmail.getEmail());
        Assertions.assertEquals(retrievedUserByEmail.getEmail(), expectedUser.getEmail());
    }

    @Test
    void testThat_WhenFreshUserRegisters_ReturnHasFilledUerProfileToFalse() {
        authResponse = AuthResponse.builder()
                .message("Registered Successfully")
                .email(user.getEmail())
                .fullName(user.getFullName())
                .picture(user.getProfileUrl())
                .hasFilledUserProfile(false)
                .build();
        AuthResponse expectedResponse = authResponse;

        AuthResponse actualResponse = serviceUnderTest.register(new JwtAuthenticationToken(jwt));

        Assertions.assertEquals(expectedResponse.isHasFilledUserProfile(), actualResponse.isHasFilledUserProfile());

    }

    @Test
    void testThat_WhenAnExistingUserRegisters_ReturnHasFilledUerProfileToTrue() {
        userRepository.save(user);

        authResponse = AuthResponse.builder()
                .message("Registered Successfully")
                .email(user.getEmail())
                .fullName(user.getFullName())
                .picture(user.getProfileUrl())
                .hasFilledUserProfile(true)
                .build();

        AuthResponse expectedResponse = authResponse;
        AuthResponse actualResponse = serviceUnderTest.register(new JwtAuthenticationToken(jwt));
        Assertions.assertEquals(expectedResponse.isHasFilledUserProfile(), actualResponse.isHasFilledUserProfile());

    }

    @Test
    void testThat_FindByEmail_ShouldReturnNUllWhenEmailIsNotFoundInDb() {
        var retrievedUserByEmail = serviceUnderTest.findByEmail("albert@gmail.com");
        Assertions.assertNull(retrievedUserByEmail);
    }


}