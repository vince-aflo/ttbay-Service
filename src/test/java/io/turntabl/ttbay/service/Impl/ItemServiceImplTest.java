package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.enums.OfficeLocation;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.ItemRepository;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.ItemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
class ItemServiceImplTest {

    private final User testUser = new User("aikscode", "aikins.dwamena@turntabl.io", "Aikins Akenten Dwamena", "", OfficeLocation.SONNIDOM_HOUSE);
    private final List<Item> testAuctionList = List.of(
            new Item(1, "Book", "Harry Potter", testUser, "", true, false),
            new Item(2, "Book1", "Harry Potter2", testUser, "", true, true),
            new Item(3, "Book2", "Harry Potter3", testUser, "", false, false),
            new Item(4, "Book3", "Harry Potter4", testUser, "", false, true)
    );
    private final List<Item> testAuctionList2 = List.of(

            new Item(2, "Book1", "Harry Potter2", testUser, "", true, true),
            new Item(3, "Book2", "Harry Potter3", testUser, "", false, false),
            new Item(4, "Book3", "Harry Potter4", testUser, "", false, true)
    );
    private JwtAuthenticationToken jwtAuthenticationToken;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        //create jwt
        String tokenValue = "token";
        String email = "aikins.dwamena@turntabl.io";
        String picture = "xxxxxx";
        String given_name = "John";
        String family_name = "Doe";
        Instant issuedAt = Instant.now();
        Instant expiredAt = Instant.now().plusSeconds(100000);
        Map<String, Object> headers = Map.of("aud", "aud");
        Map<String, Object> claims = Map.of("email", email, "picture", picture, "given_name",
                given_name, "family_name", family_name);

        //initialize jwt token
        Jwt jwt = new Jwt(tokenValue, issuedAt, expiredAt, headers, claims);

        //set jwtauthtoken
        jwtAuthenticationToken = new JwtAuthenticationToken(jwt);
    }

    //check for 404 exception when finding user :

    @Test
    void returnAllAuctionItemsByUser_givenJwtAuthTokenAndNoUserInDb_shouldThrowError(){
        doReturn(Optional.empty())
                .when(userRepository).findByEmail(any());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.returnAllAuctionItemsByUser(jwtAuthenticationToken));
    }


    //check for 404 exception when finding user with no items


    @Test
    void returnAllAuctionItemsByUser_givenJwtAuthTokenAndUserHasNoItems_shouldThrowError() {
        doReturn(Optional.of(testUser))
                .when(userRepository).findByEmail(any());
        doReturn(Optional.empty())
                .when(itemRepository)
                .findAllByUser(testUser);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.returnAllAuctionItemsByUser(jwtAuthenticationToken));
    }

    //check for 404 exception when finding user with no auction items


    @Test
    void returnAllAuctionItemsByUser_givenJwtAuthTokenAndUserHasNoAuctionItems_shouldThrowError() {
        doReturn(Optional.of(testUser))
                .when(userRepository).findByEmail(any());
        doReturn(Optional.of(testAuctionList2))
                .when(itemRepository)
                .findAllByUser(testUser);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.returnAllAuctionItemsByUser(jwtAuthenticationToken));
    }

    //check for sweet part

    @Test
    void returnAllAuctionItemsByUser_givenJwtAuthToken_shouldReturnAllAuctionItemsByUser() throws ResourceNotFoundException {
        doReturn(Optional.of(testUser))
                .when(userRepository)
                .findByEmail("aikins.dwamena@turntabl.io");

        doReturn(Optional.of(testAuctionList))
                .when(itemRepository)
                .findAllByUser(testUser);
        itemService.returnAllAuctionItemsByUser(jwtAuthenticationToken);

        verify(userRepository, times(1)).findByEmail("aikins.dwamena@turntabl.io");
        verify(itemRepository, times(1)).findAllByUser(testUser);
        Assertions.assertNotEquals(null, itemService.returnAllAuctionItemsByUser(jwtAuthenticationToken));
    }

}