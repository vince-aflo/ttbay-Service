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
import static org.mockito.Mockito.*;

@SpringBootTest
class AuctionServiceImplTest {
    private final Long id = 1L;
    private final User firstValidUser = User.builder().username("aikscode").email("test@gmail.com").fullName("Aikins Akenten Dwamena").officeLocation(OfficeLocation.SONNIDOM_HOUSE).build();
    private final User winner = User.builder().username("winner").email("winner@gmail.com").fullName("Aikins Akenten winner").officeLocation(OfficeLocation.SONNIDOM_HOUSE).build();
    private final User secondValidUser = new User("aikscode", "aiks@gmail.com", "Aikins Akenten Dwamena", "", OfficeLocation.SONNIDOM_HOUSE);
    private final Item testItem = new Item("Book1", "Harry Potter2", firstValidUser, null, false, false);
    private final Auction auction = Auction.builder().id(id).auctioner(firstValidUser).item(testItem)
            .startDate(new Date()).endDate(new Date()).reservedPrice(85.8)
            .status(AuctionStatus.LIVE).build();
    private final List<Bid> testBidList = List.of(Bid.builder().auction(auction).bidAmount(4565.33).id(id).build());
    private final Auction auction1 = Auction.builder().id(id).auctioner(secondValidUser).item(testItem).startDate(new Date()).endDate(new Date()).reservedPrice(81.5).status(AuctionStatus.LIVE).build();
    private final Auction draftAuction1 = Auction.builder().id(id).auctioner(firstValidUser).item(testItem)
            .startDate(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)).reservedPrice(85.8)
            .status(SCHEDULED).bids(List.of()).build();
    private final Auction draftAuction2 = Auction.builder().id(id).auctioner(secondValidUser).item(testItem).startDate(new Date(System.currentTimeMillis()-100000)).reservedPrice(81.5).status(SCHEDULED).build();
    private final Auction liveAuction1 = Auction.builder().id(id).auctioner(firstValidUser).item(testItem)
            .endDate(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)).reservedPrice(85.8)
            .status(LIVE).bids(List.of()).build();
    private final Auction liveAuction2 = Auction.builder().id(id).auctioner(secondValidUser).item(testItem).endDate(new Date(System.currentTimeMillis()-100000)).reservedPrice(81.5).status(LIVE).build();
    private final Auction endedAuction1 = Auction.builder().id(id).auctioner(firstValidUser).item(testItem).reservedPrice(85.8).status(END).build();
    Bid bid = Bid.builder().id(id).bidder(secondValidUser).auction(endedAuction1).bidAmount(90.0).build();
    Bid winningBid =   Bid.builder().id(2L).bidder(winner).bidAmount(100.0).auction(endedAuction1).build();
    List<Bid> bidsOnEndedAuction = List.of(bid,winningBid);
    private final Auction endedAuction2 = Auction.builder().id(id).auctioner(secondValidUser).item(testItem).reservedPrice(81.5).status(END).build();
    List<Auction> draftAuctions = List.of(draftAuction1, draftAuction2);
    List<Auction> liveAuctions = List.of(liveAuction2, liveAuction1);
    List<Auction> endedAuctions = List.of(endedAuction1,endedAuction2);
    private final Auction auction2 = Auction.builder().id(id).auctioner(firstValidUser).item(testItem)
            .startDate(new Date()).endDate(new Date()).reservedPrice(85.8)
            .status(AuctionStatus.LIVE)
            .bids(List.of(new Bid(id, 300.00, secondValidUser, null))).build();
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
    void setUp(){
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
    void returnAllAuctionByUser_givenJwtAuthTokenAndNoUserInDb_shouldThrowError(){
        doReturn(Optional.empty()).when(userRepository).findByEmail(any());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> serviceUnderTest.returnAllAuctionByUser(jwtAuthenticationToken));
    }

    @Test
    void returnAllAuctionByUser_givenJwtAuthTokenAndUserAuctionsInDb_shouldReturnItems() throws ResourceNotFoundException{
        doReturn(Optional.of(user)).when(userRepository).findByEmail(any());
        doReturn(draftAuctions).when(auctionRepository).findByAuctioner(any());
        List<AuctionResponseDTO> expectedAuctions = serviceUnderTest.returnAllAuctionByUser(jwtAuthenticationToken);
        Assertions.assertNotNull(expectedAuctions);
    }

    @Test
    void returnOneAuction_givenAuctionId_shouldReturnValidAuction() throws MismatchedEmailException, ResourceNotFoundException{
        doReturn(Optional.of(draftAuction1)).when(auctionRepository).findById(id);
        serviceUnderTest.returnOneAuction(id);
        Assertions.assertEquals(draftAuction1, serviceUnderTest.returnOneAuction(id));
    }

    @Test
    void returnOneAuction_givenAuctionId_shouldThrowResourceNotException(){
        doReturn(Optional.empty()).when(auctionRepository).findById(any());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> serviceUnderTest.returnOneAuction(id));
    }

    @Test
    void updateAuction_givenAuctionDoesNotExist_shouldThrowResourceNotFoundException(){
        EditAuctionRequestDTO editAuctionRequestDTO = new EditAuctionRequestDTO(2L, 20.00, null);
        doReturn(Optional.empty()).when(auctionRepository).findById(editAuctionRequestDTO.auctionId());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> serviceUnderTest.updateAuctionWithNoBid(editAuctionRequestDTO, jwtAuthenticationToken));
    }

    @Test
    void updateAuction_givenAuctionHadBids_shouldThrowForbiddenActionException(){
        EditAuctionRequestDTO editAuctionRequestDTO = new EditAuctionRequestDTO(id, 20.00, null);
        doReturn(Optional.of(auction2)).when(auctionRepository).findById(editAuctionRequestDTO.auctionId());
        Assertions.assertThrows(ForbiddenActionException.class, () -> serviceUnderTest.updateAuctionWithNoBid(editAuctionRequestDTO, jwtAuthenticationToken));
    }

    @Test
    void updateAuction_editAuctionRequestBody_shouldReturnUpdatedEntity() throws MismatchedEmailException, ResourceNotFoundException, ForbiddenActionException{
        EditAuctionRequestDTO editAuctionRequestDTO = new EditAuctionRequestDTO(id, 20.00, null);
        doReturn(Optional.of(draftAuction1)).when(auctionRepository).findById(editAuctionRequestDTO.auctionId());
        doReturn(draftAuction1).when(auctionRepository).save(draftAuction1);
        AuctionResponseDTO response = serviceUnderTest.updateAuctionWithNoBid(editAuctionRequestDTO, jwtAuthenticationToken);
        Assertions.assertEquals(20.00, response.reservedPrice());
    }

    @Test
    void updateAuction_givenUserIsNotTheAuctioner_shouldThrowMismatchedEmailException(){
        EditAuctionRequestDTO editAuctionRequestDTO = new EditAuctionRequestDTO(id, 20.00, null);
        doReturn(Optional.of(draftAuction2)).when(auctionRepository).findById(editAuctionRequestDTO.auctionId());
        doReturn(draftAuction2).when(auctionRepository).save(draftAuction2);
        Assertions.assertThrows(MismatchedEmailException.class, () -> serviceUnderTest.updateAuctionWithNoBid(editAuctionRequestDTO, jwtAuthenticationToken));
    }

    @Test
    public void updateDraftAuctionStatusToLive_givenDraftAuctionsWithPastDates_shouldSetAuctionStatusToLive() throws ResourceNotFoundException, ExecutionException, InterruptedException{
        doReturn(draftAuctions).when(auctionRepository).findAll();
        CompletableFuture<Void> future= serviceUnderTest.updateDraftAuctionToLiveAndPersistInDatabase();
        future.get();
        Assertions.assertEquals(LIVE, draftAuction1.getStatus());
        Assertions.assertEquals(LIVE, draftAuction2.getStatus());
    }

    @Test
    public void updateDraftAuctionStatusToLive_givenEmptyAuctions_shouldThrow404(){
        doReturn(List.of()).when(auctionRepository).findAll();
        Assertions.assertThrows(ExecutionException.class, () -> serviceUnderTest.updateDraftAuctionToLiveAndPersistInDatabase().get());
    }

    @Test
    public void updateLiveAuctionStatusToEnd_givenLiveAuctionsWithPastDates_shouldSetAuctionStatusToLive() throws ResourceNotFoundException, ExecutionException, InterruptedException {
        doReturn(liveAuctions).when(auctionRepository).findAll();
        CompletableFuture<Void> future= serviceUnderTest.updateLiveAuctionToEndAndPersistInDatabase();
        future.get();
        Assertions.assertEquals(END, liveAuction1.getStatus());
        Assertions.assertEquals(END, liveAuction2.getStatus());
    }

    @Test
    public void updateLiveAuctionStatusToEnd_givenEmptyAuctions_shouldThrow404(){
        doReturn(List.of()).when(auctionRepository).findAll();
        Assertions.assertThrows(ExecutionException.class, () -> serviceUnderTest.updateLiveAuctionToEndAndPersistInDatabase().get());
    }

    @Test
    public void updateEndedAuctionWithWinners_givenEndedAuctionsWithoutWinners_shouldSetRespectiveHighestBiddersAsWinners() throws ResourceNotFoundException, ExecutionException, InterruptedException{
        doReturn(endedAuctions).when(auctionRepository).findAll();
        doReturn(bidsOnEndedAuction).when(bidRepository).findByAuction(endedAuction1);
        doReturn(bidsOnEndedAuction).when(bidRepository).findByAuction(endedAuction2);
        CompletableFuture<Void> future = serviceUnderTest.updateAuctionWithWinnerAndBidAmount();
        future.get();
        Assertions.assertEquals(endedAuction1.getWinner().getEmail(), "winner@gmail.com");
    }

    @Test
    public void updateAuctionStatus_givenEmptyAuctionsWithPastDates_shouldSetAuctionStatusToLive() throws ResourceNotFoundException{
        doReturn(List.of(auction1, auction)).when(auctionRepository).findAll();
        serviceUnderTest.updateDraftAuctionToLiveAndPersistInDatabase();
        Assertions.assertEquals(auction.getStatus(), LIVE);
        Assertions.assertEquals(auction1.getStatus(), LIVE);
    }

    @Test
    public void updateEndedAuctionWithWinners_givenEmptyAuctions_shouldThrow404(){
        doReturn(List.of()).when(auctionRepository).findAll();
        Assertions.assertThrows(ExecutionException.class, () -> serviceUnderTest.updateAuctionWithWinnerAndBidAmount().get());
    }

    @Test
    public void cancelAuctionWithBidChecking_givenAppropriateAuctionIdButBidsAvailable_shouldReturnErrorMessage() throws MismatchedEmailException, ResourceNotFoundException{
        doReturn(Optional.of(auction)).when(auctionRepository).findById(any());
        doReturn(testBidList).when(bidRepository).findByAuction(any());
        //execute cancelAuctionWithBidChecking
        String result = serviceUnderTest.cancelAuctionWithBidChecking(id, jwtAuthenticationToken);
        //verify methods called and result
        verify(auctionRepository, times(1)).findById(any());
        verify(bidRepository, times(1)).findByAuction(any());
        verify(auctionRepository, never()).delete(auction);
        Assertions.assertEquals("Auction has bid(s), cannot be deleted", result);
    }

    @Test
    public void cancelAuctionWithBidChecking_givenAppropriateAuctionIdAndMatchingEmailAndNoBidAvailable_shouldCancelAuction() throws MismatchedEmailException, ResourceNotFoundException{
        doReturn(Optional.of(auction)).when(auctionRepository).findById(any());
        doReturn(List.of()).when(bidRepository).findByAuction(any());
        //execute cancelAuctionWithBidChecking
        String result = serviceUnderTest.cancelAuctionWithBidChecking(id, jwtAuthenticationToken);
        //verify methods called and result
        verify(auctionRepository, times(1)).findById(any());
        verify(bidRepository, times(1)).findByAuction(auction);
        verify(auctionRepository, times(1)).delete(any());
        Assertions.assertEquals(false, auction.getItem().getOnAuction());
        Assertions.assertEquals("Auction cancelled successfully", result);
    }
}
