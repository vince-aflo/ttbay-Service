package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.enums.OfficeLocation;
import io.turntabl.ttbay.enums.Role;
import io.turntabl.ttbay.exceptions.UsernameAlreadyExistException;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.UsernameService;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UsernameServiceImplTest {
    User testUser1;
    User testUser2;
    User testUser3;
    @Autowired
    private UserRepository userRepository;
    @MockBean
    private UsernameService classUnderTest;
    private User user;
    private JwtAuthenticationToken jwtAuthenticationToken;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        classUnderTest = new UsernameServiceImpl(userRepository);
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

        user = User.builder().username("emmanuel").fullName(given_name + family_name).profileUrl(picture).email(email).role(Role.USER).build();

        userRepository.save(user);
        testUser1 = new User("Tkayy", "emma@gmail.com", "Emmanuel Tweneboah", "pic", OfficeLocation.SONNIDOM_HOUSE);
        testUser2 = new User(null, "saeps@gmail.com", "Sarpong Albert", "pic", OfficeLocation.SONNIDOM_HOUSE);
        testUser3 = new User("fes", "fes@gmail.com", "Festus Obeng", "pic", OfficeLocation.SONNIDOM_HOUSE);
        userRepository.save(testUser1);
        userRepository.save(testUser2);
        userRepository.save(testUser3);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testThat_UserRequestingForUsernameUpdate_AlreadyExistInDatabase() {
        String emailOfActiveUSer = (String) new JwtAuthenticationToken(jwt).getTokenAttributes().get("email");
        boolean exists = userRepository.findByEmail(emailOfActiveUSer).isPresent();
        assertThat(exists).isTrue();
    }


    @Test
    void testThat_FindAllUnavailableUsers_ReturnsAMapOfAllUsersWithUsernames() {
        Map<String, String> expectedUsers = Stream.of(new String[][]{{user.getEmail(), user.getUsername()}, {testUser1.getEmail(), testUser1.getUsername()}, {testUser3.getEmail(), testUser3.getUsername()}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));

        Map<String, String> allUsers = classUnderTest.findAllUnavailableUsernames();
        Assertions.assertEquals(expectedUsers, allUsers);
    }

    @Test
    void testThat_TheUserExtractedFromToken_IsRemovedFromUnavailableUsernames() {
        Map<String, String> expectedUnavailableUsers = Stream.of(new String[][]{{testUser1.getEmail(), testUser1.getUsername()}, {testUser3.getEmail(), testUser3.getUsername()}}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
        Map<String, String> allUsers = classUnderTest.findAllUnavailableUsernames();
        Map<String, String> actual = classUnderTest.removeActiveUsersUsername(new JwtAuthenticationToken(jwt), allUsers);
        Assertions.assertEquals(expectedUnavailableUsers, actual);

    }


    @Test()
    void testThat_UpdatingUnavailableUsername_ThrowsUsernameAlreadyException() {
        assertThrows(UsernameAlreadyExistException.class, () -> classUnderTest.updateUsername(new JwtAuthenticationToken(jwt), "fes"));

    }

    @Test()
    void testThat_UpdateUsernameWithExistingUsername_IsAvailable() {
        String username = user.getUsername();
        String expectedResponse = "available";
        String actualResponse = classUnderTest.updateUsername(new JwtAuthenticationToken(jwt), username);
        assertEquals(expectedResponse, actualResponse);

    }


}