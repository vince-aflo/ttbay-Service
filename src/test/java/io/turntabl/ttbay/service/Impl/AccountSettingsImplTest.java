package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.enums.OfficeLocation;
import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.AccountSettingsService;
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
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountSettingsImplTest {
    private final User userWithValidDetails = User.builder().username("aikscode").email("aikins.dwamena@turntabl.io").fullName("Aikins Akenten Dwamena").profileUrl("").officeLocation(OfficeLocation.SONNIDOM_HOUSE).build();
    private JwtAuthenticationToken jwtAuthenticationToken;
    @InjectMocks
    private AccountSettingsImpl accountSettingsService;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        //create jwt
        String tokenValue = "token";
        String email = "aikins.dwamena@turntabl.io";
        String picture = "xxxxxx";
        String given_name = "John";
        String family_name = "Doe";
        Instant issuedAt = Instant.now();
        Instant expiredAt = Instant.now().plusSeconds(100000);
        Map<String, Object> headers = Map.of("aud", "aud");
        Map<String, Object> claims = Map.of("email", email, "picture", picture, "given_name", given_name, "family_name", family_name);
        //initialize jwt token
        Jwt jwt = new Jwt(tokenValue, issuedAt, expiredAt, headers, claims);
        //set jwtauthtoken
        jwtAuthenticationToken = new JwtAuthenticationToken(jwt);
    }

    //write test for when email in path is the same as email from token and user exist in db
    @Test
    void deleteAccount_givenjwtAuthTokenAndValidUserEnteredEmail_shouldReturnSuccessMessage() throws MismatchedEmailException, ResourceNotFoundException {
        String userEnteredEmail = "aikins.dwamena@turntabl.io";
        doReturn(Optional.of(userWithValidDetails))
                .when(userRepository)
                .findByEmail(userEnteredEmail);
        accountSettingsService.deleteAccount(jwtAuthenticationToken, userEnteredEmail);
        verify(userRepository, times(1)).deleteById(userEnteredEmail);
        Assertions.assertEquals(accountSettingsService.deleteAccount(jwtAuthenticationToken, userEnteredEmail), userWithValidDetails.getEmail() + " user deleted successfully");
    }

    //write test to show failure from when email is different and that exception is thrown
    @Test
    void deleteAccount_givenJwtAuthTokenAndInvalidUserEmail_shouldThrowMismatchException() {
        String userEnteredEmail = "aikinsakenten@gmail.com";
        Assertions.assertThrows(MismatchedEmailException.class, () -> accountSettingsService.deleteAccount(jwtAuthenticationToken, userEnteredEmail));
    }

    //write test to show if email matches but user doesn't exist in database
    @Test
    void deleteAccount_givenJwtAuthTokenAndNonExistingUserEmail_shouldThrowNotFoundError() {
        String userEnteredEmail = "aikins.dwamena@turntabl.io";
        doReturn(Optional.empty())
                .when(userRepository)
                .findByEmail(userEnteredEmail);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> accountSettingsService.deleteAccount(jwtAuthenticationToken, userEnteredEmail));
    }
}