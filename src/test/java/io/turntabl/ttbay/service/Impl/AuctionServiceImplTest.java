package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.enums.AuctionStatus;
import io.turntabl.ttbay.enums.OfficeLocation;
import io.turntabl.ttbay.enums.Role;
import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.AuctionRepository;
import io.turntabl.ttbay.repository.ItemRepository;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.AuctionService;
import io.turntabl.ttbay.service.ItemService;
import io.turntabl.ttbay.service.TokenAttributesExtractor;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
class AuctionServiceImplTest {
    private final User testUser = new User("aikscode", "test@gmail.com", "Aikins Akenten Dwamena", "", OfficeLocation.SONNIDOM_HOUSE);

    private final User testUser1 = new User("aikscode", "aiks@gmail.com", "Aikins Akenten Dwamena", "", OfficeLocation.SONNIDOM_HOUSE);
    private final Item testItem = new Item("Book1", "Harry Potter2", testUser, null, false, false);
    private final Auction auction = new Auction(1L, testUser, testItem, new Date(), new Date(), 85.8, null, null, AuctionStatus.LIVE);
    private final Auction auction1 = new Auction(1L, testUser1, testItem, new Date(), new Date(), 85.8, null, null, AuctionStatus.LIVE);
    List<Auction> auctions = List.of(new Auction(), new Auction());
    @Autowired
    private AuctionService serviceUnderTest;
    @MockBean
    private AuctionRepository auctionRepository;
    @Autowired
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemService itemService;
    @Autowired
    private TokenAttributesExtractor tokenAttributesExtractor;
    @MockBean
    private ItemRepository itemRepository;
    private User user;
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

        Jwt jwt = new Jwt(tokenValue, issuedAt, expiredAt, headers, claims);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(Role.USER.toString());
        jwtAuthenticationToken = new JwtAuthenticationToken(jwt, List.of(authority));
        user = User.builder().fullName(given_name + family_name).profileUrl(picture).email(email).build();


    }


    @Test
    void returnAllAuctionByUser_givenJwtAuthTokenAndNoUserInDb_shouldThrowError() {
        doReturn(Optional.empty()).when(userRepository).findByEmail(any());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> serviceUnderTest.returnAllAuctionByUser(jwtAuthenticationToken));
    }

    @Test
    void returnAllAuctionByUser_givenJwtAuthTokenAndNoUserAuctionsInDb_shouldThrowError() {
        doReturn(Optional.of(user)).when(userRepository).findByEmail(any());
        doReturn(Optional.empty()).when(auctionRepository).findAllByAuctioner(any());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> serviceUnderTest.returnAllAuctionByUser(jwtAuthenticationToken));
    }

    @Test
    void returnAllAuctionByUser_givenJwtAuthTokenAndUserAuctionsInDb_shouldReturnItems() throws ResourceNotFoundException {
        doReturn(Optional.of(user)).when(userRepository).findByEmail(any());
        doReturn(Optional.of(auctions)).when(auctionRepository).findAllByAuctioner(any());

        List<Auction> expectedAuctions = serviceUnderTest.returnAllAuctionByUser(jwtAuthenticationToken);

        Assertions.assertNotNull(expectedAuctions);

    }

    @Test
    void returnOneAuction_givenAuctionId_shouldReturnValidAuction() throws MismatchedEmailException, ResourceNotFoundException {
        doReturn(Optional.of(auction)).when(auctionRepository).findById(1L);

        serviceUnderTest.returnOneAuction(1L, jwtAuthenticationToken);

        Assertions.assertEquals(auction, serviceUnderTest.returnOneAuction(1L, jwtAuthenticationToken));
    }

    @Test
    void returnOneAuction_givenAuctionId_shouldThrowMismatchedEmailException() {
        doReturn(Optional.of(auction1)).when(auctionRepository).findById(any());
        Assertions.assertThrows(MismatchedEmailException.class, () -> serviceUnderTest.returnOneAuction(1L, jwtAuthenticationToken));
    }

    @Test
    void returnOneAuction_givenAuctionId_shouldThrowResourceNotException() {
        doReturn(Optional.empty()).when(auctionRepository).findById(any());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> serviceUnderTest.returnOneAuction(1L, jwtAuthenticationToken));
    }


//TOdo test remaining methods


}