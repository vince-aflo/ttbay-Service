package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.enums.OfficeLocation;
import io.turntabl.ttbay.exceptions.UsernameAlreadyExistException;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.TokenAttributesExtractor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class UsernameServiceImplTest {
    User testUser1 = new User("Tkayy", "test@gmail.com", "Emmanuel Tweneboah", "pic", OfficeLocation.SONNIDOM_HOUSE);
    User testUser2 = new User(null, "saeps@gmail.com", "Sarpong Albert", "pic", OfficeLocation.SONNIDOM_HOUSE);
    User testUser3 = new User("fes", "fes@gmail.com", "Festus Obeng", "pic", OfficeLocation.SONNIDOM_HOUSE);
    List<User> allUsers = List.of(testUser1, testUser2, testUser3);

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UsernameServiceImpl classUnderTest;
    @Mock
    private TokenAttributesExtractor tokenAttributesExtractor;

    private Jwt jwt;
    private JwtAuthenticationToken jwtAuthenticationToken;

    @BeforeEach
    void setUp() {
        String tokenValue = "token";
        String email = "test@gmail.com";
        String picture = "xxxxxx";
        String given_name = "Emma";
        String family_name = "tk";
        Instant issuedAt = Instant.now();
        Instant expiredAt = Instant.now().plusSeconds(100000);
        Map<String, Object> headers = Map.of("aud", "aud");
        Map<String, Object> claims = Map.of("email", email, "picture", picture, "given_name", given_name, "family_name", family_name);

        jwt = new Jwt(tokenValue, issuedAt, expiredAt, headers, claims);

        jwtAuthenticationToken = new JwtAuthenticationToken(jwt);

    }

    @Test
    void testThat_findAllUsernamesWithEmails_actualMapValues() {
        Map<String, String> expectedUsernamesWithEmail = Stream.of(new String[][]{{testUser1.getEmail(), testUser1.getUsername()}, {testUser3.getEmail(), testUser3.getUsername()}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        doReturn(allUsers).when(userRepository).findAll();
        Map<String, String> actualUsernamesWithEmail = classUnderTest.findAllUsernamesWithEmails();
        Assertions.assertEquals(expectedUsernamesWithEmail, actualUsernamesWithEmail);
    }

    @Test()
    void testThat_UpdatingUnavailableUsername_ThrowsUsernameAlreadyException() {
        doReturn(allUsers).when(userRepository).findAll();
        doReturn(testUser1.getEmail()).when(tokenAttributesExtractor).extractEmailFromToken(jwtAuthenticationToken);
        doReturn(Optional.of(testUser1)).when(userRepository).findByEmail(testUser1.getEmail());
        Map<String, String> usernamesWithEmails = classUnderTest.findAllUsernamesWithEmails();
        classUnderTest.removeActiveUsersUsernameAndEmail(jwtAuthenticationToken, usernamesWithEmails);

        assertThrows(UsernameAlreadyExistException.class, () -> classUnderTest.updateUsername(new JwtAuthenticationToken(jwt), "fes"));

    }

    @Test()
    void testThat_UpdateUsername_returnsAvailable_whenActiveUserUpdatesWithPreviousUsername() {
        doReturn(allUsers).when(userRepository).findAll();
        doReturn(testUser1.getEmail()).when(tokenAttributesExtractor).extractEmailFromToken(jwtAuthenticationToken);
        doReturn(Optional.of(testUser1)).when(userRepository).findByEmail(testUser1.getEmail());
        Map<String, String> usernamesWithEmails = classUnderTest.findAllUsernamesWithEmails();
        classUnderTest.removeActiveUsersUsernameAndEmail(jwtAuthenticationToken, usernamesWithEmails);
        String expectedResponse = "available";
        String actualResponse = classUnderTest.updateUsername(new JwtAuthenticationToken(jwt), "Tkayy");
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test()
    void testThat_UpdateUsername_returnsAvailable_whenActiveUserUpdatesWithAvailableUsername() {
        doReturn(allUsers).when(userRepository).findAll();
        doReturn(testUser1.getEmail()).when(tokenAttributesExtractor).extractEmailFromToken(jwtAuthenticationToken);
        doReturn(Optional.of(testUser1)).when(userRepository).findByEmail(testUser1.getEmail());
        Map<String, String> usernamesWithEmails = classUnderTest.findAllUsernamesWithEmails();
        classUnderTest.removeActiveUsersUsernameAndEmail(jwtAuthenticationToken, usernamesWithEmails);
        String expectedResponse = "available";
        String actualResponse = classUnderTest.updateUsername(new JwtAuthenticationToken(jwt), "notInDb");
        Assertions.assertEquals(expectedResponse, actualResponse);
    }


}