package io.turntabl.ttbay.configuration.security.Jwt;

import io.turntabl.ttbay.enums.Role;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationConverterTest {
    @Mock
    private UserRepository userRepository;
    @Autowired
    private CustomAuthenticationConverter classUnderTest;

    private User user;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        classUnderTest = new CustomAuthenticationConverter(userRepository);

        String tokenValue = "token";
        String email = "test@gmail";
        String picture = "xxxxxx";
        String given_name = "Emmanuel";
        String family_name = "Tweneboah";
        Instant issuedAt = Instant.now();
        Instant expiredAt = Instant.now().plusSeconds(100000);
        Map<String, Object> headers = Map.of("aud", "aud");
        Map<String, Object> claims = Map.of("email", email, "picture", picture, "given_name", given_name, "family_name", family_name);

        jwt = new Jwt(tokenValue, issuedAt, expiredAt, headers, claims);

        user = User.builder().fullName(given_name + family_name).profileUrl(picture).email(email).role(Role.USER).build();
    }

    @Test
    void TestThatAuthenticatedUserHasADefaultRoleUser() {
        String email = "test@gmail";
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));
        var auth = classUnderTest.convert(jwt);
        assert auth != null;
        String role = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().get();
        Assertions.assertEquals(Role.USER.toString(), role);
    }

    @Test
    void TestThatAnInvalidTokenShouldThrowInvalidBearerTokenException() {
        Jwt invalidJwt = new Jwt("invalidTokenValue", Instant.now(), Instant.now().plusSeconds(10000), Map.of("aud", "aud"), Map.of("given_name", "Tweneboah"));

        assertThatThrownBy(() -> classUnderTest.convert(invalidJwt)).isInstanceOf(InvalidBearerTokenException.class).hasMessage("Invalid bearer token");
    }
}