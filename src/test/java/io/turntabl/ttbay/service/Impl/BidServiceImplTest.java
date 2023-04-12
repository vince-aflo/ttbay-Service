package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.BidDTO;
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
public class BidServiceImplTest {
    private final User testUser = new User("aikscode", "test@gmail.com", "Aikins Akenten Dwamena", "", OfficeLocation.SONNIDOM_HOUSE);
    private final User testUser1 = new User("aikscode", "aikins.dwamena@turntabl.io", "Aikins Akenten Dwamena", "", OfficeLocation.SONNIDOM_HOUSE);
    private final Item testItem = new Item("Book1", "Harry Potter2", testUser, null, true, true);
    private final Auction testAuction = Auction.builder().id(1L).auctioner(testUser).item(testItem).startDate(new Date()).endDate(new Date()).reservedPrice(85.8).status(AuctionStatus.LIVE).build();

    private final Auction testAuction1 = Auction.builder().id(1L).auctioner(testUser1).item(testItem).startDate(new Date()).endDate(new Date()).reservedPrice(85.8).status(AuctionStatus.LIVE).build();

    List<Bid> testBids = List.of(
            Bid.builder().bidAmount(5000.0).build(),
            Bid.builder().bidAmount(2000.0).build(),
            Bid.builder().bidAmount(3000.0).build()
    );
    @MockBean
    private BidRepository bidRepository;
    @MockBean
    private AuctionRepository auctionRepository;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private BidService bidService;
    private JwtAuthenticationToken jwtAuthenticationToken;

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
        Map<String, Object> claims = Map.of("email", email, "picture", picture, "given_name", given_name, "family_name", family_name);

        //initialize jwt token
        Jwt jwt = new Jwt(tokenValue, issuedAt, expiredAt, headers, claims);

        //set jwtauthtoken
        jwtAuthenticationToken = new JwtAuthenticationToken(jwt);


    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        bidRepository.deleteAll();
        auctionRepository.deleteAll();
    }

    @Test
    void makeBid_givenBidDTOAndNewMaxBid_shouldReturnBidHasBeenMadeSuccessfully() throws BidLessThanMaxBidException, ResourceNotFoundException, BidCannotBeZero, UserCannotBidOnTheirAuction, ForbiddenActionException, MessagingException {
        BidDTO testBidDTO = new BidDTO(6000.0, 1L);
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail(any());
        doReturn(Optional.of(testAuction)).when(auctionRepository).findById(any());
        doReturn(testBids).when(bidRepository).findByAuction(testAuction);

        bidService.makeBid(testBidDTO, jwtAuthenticationToken);

        verify(auctionRepository, times(1)).findById(testBidDTO.auctionId());
        verify(bidRepository, times(1)).findByAuction(testAuction);
        verify(bidRepository, times(1)).save(any());

        Assertions.assertEquals("Bid has been made successfully", bidService.makeBid(testBidDTO, jwtAuthenticationToken));
    }

    @Test
    void makeBid_givenBidDTOAndNewNotMaxBid_shouldThrowBidLessThanMaxBidException()  {
        BidDTO testBidDTO = new BidDTO(1000.0, 1L);
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail(any());
        doReturn(Optional.of(testAuction)).when(auctionRepository).findById(any());
        doReturn(testBids).when(bidRepository).findByAuction(testAuction);

        assertThrows(BidLessThanMaxBidException.class, () -> bidService.makeBid(testBidDTO, jwtAuthenticationToken));


    }

    @Test
    void makeBid_givenBidDTOAndOldMaxBid_shouldReturnBidHasBeenMadeSuccessfully() throws BidLessThanMaxBidException, ResourceNotFoundException, BidCannotBeZero, UserCannotBidOnTheirAuction, ForbiddenActionException, MessagingException {
        BidDTO testBidDTO = new BidDTO(5000.0, 1L);
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail(any());
        doReturn(Optional.of(testAuction)).when(auctionRepository).findById(any());
        doReturn(testBids).when(bidRepository).findByAuction(testAuction);

        bidService.makeBid(testBidDTO, jwtAuthenticationToken);

        verify(auctionRepository, times(1)).findById(testBidDTO.auctionId());
        verify(bidRepository, times(1)).findByAuction(testAuction);
        verify(bidRepository, times(1)).save(any());


        Assertions.assertEquals("Bid has been made successfully", bidService.makeBid(testBidDTO, jwtAuthenticationToken));
    }

    @Test
    void makeBid_givenBidDTOInValidAuction_shouldThrowAuctionNotFoundException() {
        BidDTO testBidDTO = new BidDTO(6000.0, 1L);
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail(any());
        doReturn(Optional.empty()).when(auctionRepository).findById(any());


       assertThrows(ResourceNotFoundException.class, () -> bidService.makeBid(testBidDTO,jwtAuthenticationToken));
    }

    @Test
    void makeBid_givenBidDTOAndBadToken_shouldThrowUserNotFoundException()  {
        BidDTO testBidDTO = new BidDTO(6000.0, 1L);
        doReturn(Optional.empty()).when(userRepository).findByEmail(any());



        assertThrows(ResourceNotFoundException.class, () -> bidService.makeBid(testBidDTO,jwtAuthenticationToken));
    }

    @Test
    void makeBid_givenBidDTOAndBadToken_shouldThrowBidCannotBeZeroException()  {
        BidDTO testBidDTO = new BidDTO(0.0, 1L);
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail(any());




        assertThrows(BidCannotBeZero.class, () -> bidService.makeBid(testBidDTO,jwtAuthenticationToken));
    }

    @Test
    void makeBid_givenBidDTOAndBadToken_shouldThrowUserCannotBidOnTheirItemException()  {
        BidDTO testBidDTO = new BidDTO(6000.0, 1L);
        doReturn(Optional.of(testUser1)).when(userRepository).findByEmail(testUser1.getEmail());
        doReturn(Optional.of(testAuction1)).when(auctionRepository).findById(any());



        assertThrows(UserCannotBidOnTheirAuction.class, () -> bidService.makeBid(testBidDTO,jwtAuthenticationToken));
    }

    @Test
    void makeBid_givenBidDTO_shouldThrowBidLessThanMaxException() {
        BidDTO testBidDTO = new BidDTO(200.0, 1L);
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail(any());
        doReturn(Optional.of(testAuction)).when(auctionRepository).findById(any());
        doReturn(testBids).when(bidRepository).findByAuction(testAuction);


        assertThrows(BidLessThanMaxBidException.class, () -> bidService.makeBid(testBidDTO, jwtAuthenticationToken));
    }

}