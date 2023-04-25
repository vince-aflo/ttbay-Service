package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.BidDTO;
import io.turntabl.ttbay.dto.BidResponseDTO;
import io.turntabl.ttbay.enums.AuctionStatus;
import io.turntabl.ttbay.enums.OfficeLocation;
import io.turntabl.ttbay.exceptions.*;
import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.Bid;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.AuctionRepository;
import io.turntabl.ttbay.repository.BidRepository;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.BidMapperService;
import io.turntabl.ttbay.service.BidService;
import jakarta.mail.MessagingException;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BidServiceImplTest{
    public  final long id1 = 1L;
    private final User bidder = User.builder().username("aiks").email("test@gmail.com").fullName("Aikins Akenten Dwamena").officeLocation(OfficeLocation.SONNIDOM_HOUSE).build();
    private final User auctioneer = User.builder().username("aikscode").email("aikins.dwamena@turntabl.io").fullName("Aikins Akenten Dwamena").officeLocation(OfficeLocation.SONNIDOM_HOUSE).build();
    private final Item auctioneerItem = Item.builder().name("Harry Potter").user(bidder).onAuction(true).isSold(true).build();
    private final Auction biddersAuction = Auction.builder().id(id1).auctioner(bidder).item(auctioneerItem).startDate(new Date()).endDate(new Date()).reservedPrice(85.8).status(AuctionStatus.LIVE).build();
    private final Auction auctioneersAuction = Auction.builder().id(id1).auctioner(auctioneer).item(auctioneerItem).startDate(new Date()).endDate(new Date()).reservedPrice(85.8).status(AuctionStatus.LIVE).build();
    List<Bid> testBids = List.of(
            Bid.builder().id(id1).auction(auctioneersAuction).bidAmount(5000.0).build(),
            Bid.builder().id(2L).auction(auctioneersAuction).bidAmount(2000.0).build(),
            Bid.builder().id(3L).auction(auctioneersAuction).bidAmount(3000.0).build()
    );
    @Autowired
    BidMapperService bidMapperService;
    @MockBean
    private BidRepository bidRepository;
    @MockBean
    private AuctionRepository auctionRepository;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private BidService bidService;
    private JwtAuthenticationToken jwtAuthenticationToken;
    @MockBean
    private EmailTriggerServiceImpl emailTriggerService;

    @BeforeEach
    void setUp(){
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

    @AfterEach
    void tearDown(){
        userRepository.deleteAll();
        bidRepository.deleteAll();
        auctionRepository.deleteAll();
    }

    @Test
    void makeBid_givenBidDTOAndNewMaxBid_shouldReturnBidHasBeenMadeSuccessfully() throws ResourceNotFoundException, BidException, ForbiddenActionException, MessagingException{
        BidDTO testBidDTO = new BidDTO(6000.0, id1);
        doReturn(Optional.of(bidder)).when(userRepository).findByEmail(any());
        doReturn(Optional.of(biddersAuction)).when(auctionRepository).findById(any());
        doReturn(testBids).when(bidRepository).findByAuction(biddersAuction);
        bidService.makeBid(testBidDTO, jwtAuthenticationToken);
        verify(auctionRepository, times(1)).findById(testBidDTO.auctionId());
        verify(bidRepository, times(1)).findByAuction(biddersAuction);
        verify(bidRepository, times(1)).save(any());
        Assertions.assertEquals("Bid has been made successfully", bidService.makeBid(testBidDTO, jwtAuthenticationToken));
    }

    @Test
    void makeBid_givenBidDTOAndNewNotMaxBid_shouldThrowBidLessThanMaxBidException(){
        BidDTO testBidDTO = new BidDTO(1000.0, id1);
        doReturn(Optional.of(bidder)).when(userRepository).findByEmail(any());
        doReturn(Optional.of(biddersAuction)).when(auctionRepository).findById(any());
        doReturn(testBids).when(bidRepository).findByAuction(biddersAuction);
        assertThrows(BidLessThanMaxBidException.class, () -> bidService.makeBid(testBidDTO, jwtAuthenticationToken));
    }

    @Test
    void makeBid_givenBidDTOAndOldMaxBid_shouldReturnBidHasBeenMadeSuccessfully() throws BidException, ResourceNotFoundException, ForbiddenActionException, MessagingException{
        BidDTO testBidDTO = new BidDTO(5000.0, id1);
        doReturn(Optional.of(bidder)).when(userRepository).findByEmail(any());
        doReturn(Optional.of(biddersAuction)).when(auctionRepository).findById(any());
        doReturn(testBids).when(bidRepository).findByAuction(biddersAuction);
        bidService.makeBid(testBidDTO, jwtAuthenticationToken);
        verify(auctionRepository, times(1)).findById(testBidDTO.auctionId());
        verify(bidRepository, times(1)).findByAuction(biddersAuction);
        verify(bidRepository, times(1)).save(any());
        Assertions.assertEquals("Bid has been made successfully", bidService.makeBid(testBidDTO, jwtAuthenticationToken));
    }

    @Test
    void makeBid_givenBidDTOInValidAuction_shouldThrowAuctionNotFoundException(){
        BidDTO testBidDTO = new BidDTO(6000.0, id1);
        doReturn(Optional.of(bidder)).when(userRepository).findByEmail(any());
        doReturn(Optional.empty()).when(auctionRepository).findById(any());
        assertThrows(ResourceNotFoundException.class, () -> bidService.makeBid(testBidDTO,jwtAuthenticationToken));
    }

    @Test
    void makeBid_givenBidDTOAndBadToken_shouldThrowUserNotFoundException(){
        BidDTO testBidDTO = new BidDTO(6000.0, id1);
        doReturn(Optional.empty()).when(userRepository).findByEmail(any());
        assertThrows(ResourceNotFoundException.class, () -> bidService.makeBid(testBidDTO,jwtAuthenticationToken));
    }

    @Test
    void makeBid_givenBidDTOAndBadToken_shouldThrowBidCannotBeZeroException() {
        BidDTO testBidDTO = new BidDTO(0.0, id1);
        doReturn(Optional.of(bidder)).when(userRepository).findByEmail(any());
        assertThrows(BidCannotBeZero.class, () -> bidService.makeBid(testBidDTO,jwtAuthenticationToken));
    }

    @Test
    void makeBid_givenBidDTOAndBadToken_shouldThrowUserCannotBidOnTheirItemException() {
        BidDTO testBidDTO = new BidDTO(6000.0, id1);
        doReturn(Optional.of(auctioneer)).when(userRepository).findByEmail(auctioneer.getEmail());
        doReturn(Optional.of(auctioneersAuction)).when(auctionRepository).findById(any());
        assertThrows(UserCannotBidOnTheirAuction.class, () -> bidService.makeBid(testBidDTO, jwtAuthenticationToken));
    }

    @Test
    void makeBid_givenBidDTO_shouldThrowBidLessThanMaxException(){
        BidDTO testBidDTO = new BidDTO(200.0, id1);
        doReturn(Optional.of(bidder)).when(userRepository).findByEmail(any());
        doReturn(Optional.of(biddersAuction)).when(auctionRepository).findById(any());
        doReturn(testBids).when(bidRepository).findByAuction(biddersAuction);
        assertThrows(BidLessThanMaxBidException.class, () -> bidService.makeBid(testBidDTO, jwtAuthenticationToken));
    }

    @Test
    void returnAllBidsByUser_givenUnExistingUser_shouldThrowAnError(){
        doReturn(Optional.empty()).when(userRepository).findByEmail(any());
        assertThrows(ResourceNotFoundException.class, () -> bidService.returnAllBidsByUser(jwtAuthenticationToken));
    }

    @Test
    void returnAllBidsByUser_givenAnExistingUserWithBids_shouldReturnAllTheBids() throws ResourceNotFoundException{
        doReturn(Optional.of(auctioneer)).when(userRepository).findByEmail(any());
        doReturn(testBids).when(bidRepository).findByBidder(auctioneer);
        List<BidResponseDTO> bidsByTestUser1 = bidService.returnAllBidsByUser(jwtAuthenticationToken);
        verify(bidRepository, times(1)).findByBidder(auctioneer);
        verify(userRepository, times(1)).findByEmail(any());
        Assertions.assertEquals(testBids.stream().map(bidMapperService::returnBidResponse).toList(), bidsByTestUser1 );
    }

    @Test
    void getBidCount_givenAnAuctionId_shouldReturnNumberOfBidsOnTheAuction() throws ResourceNotFoundException{
        doReturn(3L).when(bidRepository).countByAuction(biddersAuction);
        doReturn(Optional.of(biddersAuction)).when(auctionRepository).findById(biddersAuction.getId());
        Long bidCount = bidService.getBidCount(biddersAuction.getId());
        Assertions.assertEquals(testBids.size(), bidCount);
    }

    @Test
    void getBidCount_givenAuctionDoesNotExist_shouldThrowResourceNotFoundException(){
        doReturn(Optional.empty()).when(auctionRepository).findById(biddersAuction.getId());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> bidService.getBidCount(biddersAuction.getId()));
    }
}