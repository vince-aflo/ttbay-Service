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
import io.turntabl.ttbay.repository.BidRepository;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static io.turntabl.ttbay.enums.AuctionStatus.*;
import static io.turntabl.ttbay.enums.AuctionStatus.LIVE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
class AuctionServiceImplTest {
    private final User testUser = new User("aikscode", "test@gmail.com", "Aikins Akenten Dwamena", "", OfficeLocation.SONNIDOM_HOUSE);
    private final User winner = new User("winner", "winner@gmail.com", "Aikins Akenten winner", "", OfficeLocation.SONNIDOM_HOUSE);

    private final User testUser1 = new User("aikscode", "aiks@gmail.com", "Aikins Akenten Dwamena", "", OfficeLocation.SONNIDOM_HOUSE);
    private final Item testItem = new Item("Book1", "Harry Potter2", testUser, null, false, false);
    private final Auction draftAuction = Auction.builder().id(1L).auctioner(testUser).item(testItem)
            .startDate(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)).reservedPrice(85.8)
            .status(DRAFT).bids(List.of()).build();
    private final Auction draftAuction1 = Auction.builder().id(1L).auctioner(testUser1).item(testItem).startDate(new Date(System.currentTimeMillis()-100000)).reservedPrice(81.5).status(DRAFT).build();

    private final Auction liveAuction = Auction.builder().id(1L).auctioner(testUser).item(testItem)
            .endDate(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)).reservedPrice(85.8)
            .status(LIVE).bids(List.of()).build();
    private final Auction liveAuction1 = Auction.builder().id(1L).auctioner(testUser1).item(testItem).endDate(new Date(System.currentTimeMillis()-100000)).reservedPrice(81.5).status(LIVE).build();

    private final Auction endedAuction = Auction.builder().id(1L).auctioner(testUser).item(testItem).reservedPrice(85.8).status(END).build();
    Bid bid = Bid.builder().id(1L).bidder(testUser1).auction(endedAuction).bidAmount(90.0).build();
    Bid winningBid =   Bid.builder().id(2L).bidder(winner).bidAmount(100.0).auction(endedAuction).build();
    List<Bid> bidsOnEndedAuction = List.of(bid,winningBid);

    private final Auction endedAuction1 = Auction.builder().id(1L).auctioner(testUser1).item(testItem).reservedPrice(81.5).status(END).build();
    List<Auction> draftAuctions = List.of(draftAuction, draftAuction1);
    List<Auction> liveAuctions = List.of(liveAuction1,liveAuction);
    List<Auction> endedAuctions = List.of(endedAuction,endedAuction1);
    private final Auction auction2 = Auction.builder().id(1L).auctioner(testUser).item(testItem)
            .startDate(new Date()).endDate(new Date()).reservedPrice(85.8)
            .status(AuctionStatus.LIVE)
            .bids(List.of(new Bid(1L, 300.00, testUser1, null))).build();
    @Autowired
    private AuctionService serviceUnderTest;
    @MockBean
    private AuctionRepository auctionRepository;
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
    @MockBean
    private BidRepository bidRepository;



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
        doReturn(draftAuctions).when(auctionRepository).findByAuctioner(any());
        List<AuctionResponseDTO> expectedAuctions = serviceUnderTest.returnAllAuctionByUser(jwtAuthenticationToken);
        Assertions.assertNotNull(expectedAuctions);

    }

    @Test
    void returnOneAuction_givenAuctionId_shouldReturnValidAuction() throws MismatchedEmailException, ResourceNotFoundException {
        doReturn(Optional.of(draftAuction)).when(auctionRepository).findById(1L);
        serviceUnderTest.returnOneAuction(1L);
        Assertions.assertEquals(draftAuction, serviceUnderTest.returnOneAuction(1L));
    }
    @Test
    void returnOneAuction_givenAuctionId_shouldThrowResourceNotException() {
        doReturn(Optional.empty()).when(auctionRepository).findById(any());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> serviceUnderTest.returnOneAuction(1L));
    }

    @Test
    void updateAuction_givenAuctionDoesNotExist_shouldThrowResourceNotFoundException() {
        EditAuctionRequestDTO editAuctionRequestDTO = new EditAuctionRequestDTO(2L, 20.00, null);
        doReturn(Optional.empty()).when(auctionRepository).findById(editAuctionRequestDTO.auctionId());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> serviceUnderTest.updateAuctionWithNoBid(editAuctionRequestDTO, jwtAuthenticationToken));
    }

    @Test
    void updateAuction_givenAuctionHadBids_shouldThrowForbiddenActionException() {
        EditAuctionRequestDTO editAuctionRequestDTO = new EditAuctionRequestDTO(1L, 20.00, null);
        doReturn(Optional.of(auction2)).when(auctionRepository).findById(editAuctionRequestDTO.auctionId());

        Assertions.assertThrows(ForbiddenActionException.class, () -> serviceUnderTest.updateAuctionWithNoBid(editAuctionRequestDTO, jwtAuthenticationToken));
    }

    @Test
    void updateAuction_editAuctionRequestBody_shouldReturnUpdatedEntity() throws MismatchedEmailException, ResourceNotFoundException, ForbiddenActionException {
        EditAuctionRequestDTO editAuctionRequestDTO = new EditAuctionRequestDTO(1L, 20.00, null);
        doReturn(Optional.of(draftAuction)).when(auctionRepository).findById(editAuctionRequestDTO.auctionId());
        doReturn(draftAuction).when(auctionRepository).save(draftAuction);

        AuctionResponseDTO response = serviceUnderTest.updateAuctionWithNoBid(editAuctionRequestDTO, jwtAuthenticationToken);
        Assertions.assertEquals(20.00, response.reservedPrice());
    }

    @Test
    void updateAuction_givenUserIsNotTheAuctioner_shouldThrowMismatchedEmailException() {
        EditAuctionRequestDTO editAuctionRequestDTO = new EditAuctionRequestDTO(1L, 20.00, null);
        doReturn(Optional.of(draftAuction1)).when(auctionRepository).findById(editAuctionRequestDTO.auctionId());
        doReturn(draftAuction1).when(auctionRepository).save(draftAuction1);
        Assertions.assertThrows(MismatchedEmailException.class, () -> serviceUnderTest.updateAuctionWithNoBid(editAuctionRequestDTO, jwtAuthenticationToken));
    }
    @Test
    public void updateDraftAuctionStatusToLive_givenDraftAuctionsWithPastDates_shouldSetAuctionStatusToLive() throws ResourceNotFoundException, ExecutionException, InterruptedException {
        doReturn(draftAuctions).when(auctionRepository).findAll();
        CompletableFuture<Void> future= serviceUnderTest.updateDraftAuctionToLiveAndPersistInDatabase();
        future.get();
        Assertions.assertEquals(LIVE, draftAuction.getStatus());
        Assertions.assertEquals(LIVE, draftAuction1.getStatus());
    }
    @Test
    public void updateDraftAuctionStatusToLive_givenEmptyAuctions_shouldThrow404() {
        doReturn(List.of()).when(auctionRepository).findAll();
        Assertions.assertThrows(ExecutionException.class, () -> serviceUnderTest.updateDraftAuctionToLiveAndPersistInDatabase().get());
    }

    @Test
    public void updateLiveAuctionStatusToEnd_givenLiveAuctionsWithPastDates_shouldSetAuctionStatusToLive() throws ResourceNotFoundException, ExecutionException, InterruptedException {
        doReturn(liveAuctions).when(auctionRepository).findAll();
        CompletableFuture<Void> future= serviceUnderTest.updateLiveAuctionToEndAndPersistInDatabase();
        future.get();
        Assertions.assertEquals(END, liveAuction.getStatus());
        Assertions.assertEquals(END, liveAuction1.getStatus());
    }

    @Test
    public void updateLiveAuctionStatusToEnd_givenEmptyAuctions_shouldThrow404() {
        doReturn(List.of()).when(auctionRepository).findAll();
        Assertions.assertThrows(ExecutionException.class, () -> serviceUnderTest.updateLiveAuctionToEndAndPersistInDatabase().get());
    }
    @Test
    public void updateEndedAuctionWithWinners_givenEndedAuctionsWithoutWinners_shouldSetRespectiveHighestBiddersAsWinners() throws ResourceNotFoundException, ExecutionException, InterruptedException {
        doReturn(endedAuctions).when(auctionRepository).findAll();
        doReturn(bidsOnEndedAuction).when(bidRepository).findByAuction(endedAuction);
        doReturn(bidsOnEndedAuction).when(bidRepository).findByAuction(endedAuction1);
        CompletableFuture<Void> future= serviceUnderTest.updateAuctionWithWinnerAndBidAmount();
        future.get();
        Assertions.assertEquals(endedAuction.getWinner().getEmail(), "winner@gmail.com");
    }

    @Test
    public void updateEndedAuctionWithWinners_givenEmptyAuctions_shouldThrow404() {
        doReturn(List.of()).when(auctionRepository).findAll();
        Assertions.assertThrows(ExecutionException.class, () -> serviceUnderTest.updateAuctionWithWinnerAndBidAmount().get());
    }
}