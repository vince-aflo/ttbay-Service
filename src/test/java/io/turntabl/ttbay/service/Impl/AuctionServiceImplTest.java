package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.AuctionResponseDTO;
import io.turntabl.ttbay.dto.EditAuctionRequestDTO;
import io.turntabl.ttbay.enums.AuctionStatus;
import io.turntabl.ttbay.enums.OfficeLocation;
import io.turntabl.ttbay.enums.Role;
import io.turntabl.ttbay.exceptions.ForbiddenActionException;
import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.Bid;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.AuctionRepository;
import io.turntabl.ttbay.repository.ItemRepository;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.AuctionService;
import io.turntabl.ttbay.service.AuctionMapperService;
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
    private final Auction auction = Auction.builder().id(1L).auctioner(testUser).item(testItem)
                                        .startDate(new Date()).endDate(new Date()).reservedPrice(85.8)
                                        .status(AuctionStatus.LIVE).build();
    private final Auction auction1 = Auction.builder().id(1L).auctioner(testUser1).item(testItem).startDate(new Date()).endDate(new Date()).reservedPrice(81.5).status(AuctionStatus.LIVE).build();
    private final Auction auction2 = Auction.builder().id(1L).auctioner(testUser).item(testItem)
            .startDate(new Date()).endDate(new Date()).reservedPrice(85.8)
            .status(AuctionStatus.LIVE)
            .bids(List.of(new Bid(1L, 300.00, testUser1, null))).build();
    List<Auction> auctions = List.of(auction , auction1);
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
    void returnAllAuctionByUser_givenJwtAuthTokenAndUserAuctionsInDb_shouldReturnItems() throws ResourceNotFoundException {
        doReturn(Optional.of(user)).when(userRepository).findByEmail(any());
        doReturn(auctions).when(auctionRepository).findByAuctioner(any());

        List<AuctionResponseDTO> expectedAuctions = serviceUnderTest.returnAllAuctionByUser(jwtAuthenticationToken);

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

    @Test
    void updateAuction_editAuctionRequestBody_shouldReturnUpdatedEntity() throws MismatchedEmailException, ResourceNotFoundException, ForbiddenActionException {
        EditAuctionRequestDTO editAuctionRequestDTO = new EditAuctionRequestDTO(1L, 20.00, null);
        doReturn(Optional.of(auction)).when(auctionRepository).findById(editAuctionRequestDTO.auctionId());
        doReturn(auction).when(auctionRepository).save(auction);

        AuctionResponseDTO response = serviceUnderTest.updateAuctionWithNoBid(editAuctionRequestDTO, jwtAuthenticationToken);

        Assertions.assertEquals(20.00, response.reservedPrice());
    }

    @Test
    void updateAuction_givenUserIsNotTheAuctioner_shouldThrowMismatchedEmailException() throws MismatchedEmailException, ResourceNotFoundException, ForbiddenActionException {
        EditAuctionRequestDTO editAuctionRequestDTO = new EditAuctionRequestDTO(1L, 20.00, null);
        doReturn(Optional.of(auction1)).when(auctionRepository).findById(editAuctionRequestDTO.auctionId());
        doReturn(auction1).when(auctionRepository).save(auction1);

        Assertions.assertThrows(MismatchedEmailException.class, () -> serviceUnderTest.updateAuctionWithNoBid(editAuctionRequestDTO, jwtAuthenticationToken));
    }

    @Test
    void updateAuction_givenAuctionDoesNotExist_shouldThrowResourceNotFoundException() throws MismatchedEmailException, ResourceNotFoundException, ForbiddenActionException {
        EditAuctionRequestDTO editAuctionRequestDTO = new EditAuctionRequestDTO(2L, 20.00, null);
        doReturn(Optional.empty()).when(auctionRepository).findById(editAuctionRequestDTO.auctionId());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> serviceUnderTest.updateAuctionWithNoBid(editAuctionRequestDTO, jwtAuthenticationToken));
    }

    @Test
    void updateAuction_givenAuctionHadBids_shouldThrowForbiddenActionException() throws MismatchedEmailException, ResourceNotFoundException, ForbiddenActionException {
        EditAuctionRequestDTO editAuctionRequestDTO = new EditAuctionRequestDTO(1L, 20.00, null);
        doReturn(Optional.of(auction2)).when(auctionRepository).findById(editAuctionRequestDTO.auctionId());

        Assertions.assertThrows(ForbiddenActionException.class, () -> serviceUnderTest.updateAuctionWithNoBid(editAuctionRequestDTO, jwtAuthenticationToken));
    }


//TOdo test remaining methods


}