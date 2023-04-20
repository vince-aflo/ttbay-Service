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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
class AccountSettingsImplTest{
    private  JwtAuthenticationToken jwtAuthenticationToken;
    @Autowired
    private AccountSettingsService accountSettingsService;
    @MockBean
    private UserRepository userRepository;
    private final User testUser = new User("aikscode","aikins.dwamena@turntabl.io","Aikins Akenten Dwamena","", OfficeLocation.SONNIDOM_HOUSE);

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
    void deleteAccount_givenjwtAuthTokenAndValidUserEnteredEmail_shouldReturnSuccessMessage() throws MismatchedEmailException, ResourceNotFoundException{
        String userEnteredEmail = "aikins.dwamena@turntabl.io";
        doReturn(Optional.of(testUser))
                .when(userRepository)
                .findByEmail(userEnteredEmail);
         accountSettingsService.deleteAccount(jwtAuthenticationToken , userEnteredEmail);
        verify(userRepository,times(1)).deleteById(userEnteredEmail);
        Assertions.assertEquals(accountSettingsService.deleteAccount(jwtAuthenticationToken, userEnteredEmail),testUser.getEmail() + " user deleted successfully");
    }

    //write test to show failure from when email is different and that exception is thrown
    @Test
    void deleteAccount_givenjwtAuthTokenAndUserEnteredEmail_shouldThrowMismatchException(){
        String userEnteredEmail = "aikinsakenten@gmail.com";
        Assertions.assertThrows(MismatchedEmailException.class, ()-> accountSettingsService.deleteAccount(jwtAuthenticationToken, userEnteredEmail));
    }

    //write test to show if email matches but user doesn't exist in database
    @Test
    void deleteAccount_givenjwtAuthTokenAndUserEnteredEmail_shouldThrowNotFoundError(){
        String userEnteredEmail = "aikins.dwamena@turntabl.io";
        doReturn(Optional.empty())
                .when(userRepository)
                .findByEmail(userEnteredEmail);
        Assertions.assertThrows(ResourceNotFoundException.class, ()-> accountSettingsService.deleteAccount(jwtAuthenticationToken, userEnteredEmail));
    }
}