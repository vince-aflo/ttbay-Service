package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.ItemRequest;
import io.turntabl.ttbay.enums.AuctionStatus;
import io.turntabl.ttbay.enums.Category;
import io.turntabl.ttbay.enums.ItemCondition;
import io.turntabl.ttbay.enums.OfficeLocation;
import io.turntabl.ttbay.exceptions.ForbiddenActionException;
import io.turntabl.ttbay.exceptions.ItemAlreadyOnAuctionException;
import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.Bid;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.*;
import io.turntabl.ttbay.service.ItemService;
import io.turntabl.ttbay.service.TokenAttributesExtractor;
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

import static org.mockito.Mockito.*;

@SpringBootTest
class ItemServiceImplTest {
//    private final User testUser = new User("aikscode", "aikins.dwamena@turntabl.io", "Aikins Akenten Dwamena", "", OfficeLocation.SONNIDOM_HOUSE);
//    private final List<Item> testAuctionList = List.of(new Item("Book", "Harry Potter", testUser, null, true, false), new Item("Book1", "Harry Potter2", testUser, null, true, true), new Item("Book2", "Harry Potter3", testUser, null, false, false), new Item("Book3", "Harry Potter4", testUser, null, false, true));
//    private final List<Item> testItemsList = List.of(new Item("Book", "Harry Potter", testUser, null, false, false), new Item("Book1", "Harry Potter2", testUser, null, true, true), new Item("Book2", "Harry Potter3", testUser, null, false, false), new Item("Book3", "Harry Potter4", testUser, null, false, true));
//    private final Item testItem = new Item("Book1", "Harry Potter2", testUser, null, true, true);
//    private final List<Item> testAuctionList2 = List.of(
//
//            new Item("Book1", "Harry Potter2", testUser, null, true, true), new Item("Book2", "Harry Potter3", testUser, null, false, false), new Item("Book3", "Harry Potter4", testUser, null, false, true));
//    User testUser1 = new User("Emma", "a@gmail.com", "Emmanuel Koduah Tweneboah", "", OfficeLocation.SONNIDOM_HOUSE);


    private JwtAuthenticationToken jwtAuthenticationToken;

    @MockBean
    @Autowired
    private UserRepository userRepository;
    @MockBean
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private TokenAttributesExtractor tokenAttributesExtractor;

    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemImageRepository itemImageRepository;

    @MockBean
    private AuctionRepository auctionRepository;

    @MockBean
    private BidRepository bidRepository;

    private final User testUser = new User(
            "aikscode",
            "aikins.dwamena@turntabl.io",
            "Aikins Akenten Dwamena",
            "",
            OfficeLocation.SONNIDOM_HOUSE);
    User testUser1 = new User("Emma",
            "a@gmail.com",
            "Emmanuel Koduah Tweneboah",
            "",
            OfficeLocation.SONNIDOM_HOUSE);
    private final List<Item> testItemList = List.of(
            new Item("Book", "Harry Potter", testUser, null, true, false),
            new Item("Book1", "Harry Potter2", testUser, null, true, true),
            new Item("Book2", "Harry Potter3", testUser, null, false, false),
            new Item("Book3", "Harry Potter4", testUser, null, false, true)
    );
    private final List<Item> testItemsList = List.of(new Item("Book", "Harry Potter", testUser, null, false, false), new Item("Book1", "Harry Potter2", testUser, null, true, true), new Item("Book2", "Harry Potter3", testUser, null, false, false), new Item("Book3", "Harry Potter4", testUser, null, false, true));
    private final Item testItem = new Item("Book1", "Harry Potter2", testUser, null, false, false);
    private final Item testItem1 = new Item("Book1", "Harry Potter2", testUser, null, true, true);
    private final List<Item> testItemList2 = List.of(

            new Item("Book1", "Harry Potter2", testUser, null, true, true), new Item("Book2", "Harry Potter3", testUser, null, false, false), new Item("Book3", "Harry Potter4", testUser, null, false, true));
    private final List<Auction> testAuctionList =List.of(
    new Auction(1L,testUser, testItem1,new Date(),new Date(),5000.0,6000.0,null, AuctionStatus.LIVE)
    );

    private final List<Auction> testAuctionList1 =List.of(
            new Auction(1L,testUser1, testItem1,new Date(),new Date(),5000.0,6000.0,null, AuctionStatus.LIVE)
    );

    private final List<Auction> testAuctionList2 =List.of(
            new Auction(1L,testUser, testItem1,new Date(),new Date(),5000.0,6000.0,null, AuctionStatus.DRAFT)
    );


    private final List<Bid> testBidList = List.of(

            new Bid(1L,9000.0,testUser,testAuctionList.get(0))

    );
    private final Item testItemOnAuction = new Item("Book1", "Harry Potter2", testUser, null, true, false);



    @BeforeEach
    void setUp() {
//        itemService = new ItemServiceImpl(itemRepository, tokenAttributesExtractor, userRepository);
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
        itemRepository.deleteAll();
    }

    //check for 404 exception when finding user :

    @Test
    void returnAllAuctionItemsByUser_givenJwtAuthTokenAndNoUserInDb_shouldThrowError() {
        doReturn(Optional.empty()).when(userRepository).findByEmail(any());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.returnAllAuctionItemsByUser(jwtAuthenticationToken));
    }

    @Test
    void returnAllItemsByUser_givenJwtAuthTokenAndNoUserInDb_shouldThrowError() {
        doReturn(Optional.empty()).when(userRepository).findByEmail(any());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.returnAllItemsByUser(jwtAuthenticationToken));
    }

    @Test
    void returnAllItemsByUser_givenJwtAuthTokenAndUserHasNoItems_shouldThrowError() {
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail(any());
        doReturn(Optional.empty()).when(itemRepository).findAllByUser(testUser);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.returnAllItemsByUser(jwtAuthenticationToken));
    }

    @Test
    void returnAllItemsByUser_givenJwtAuthToken_shouldReturnAllItemsByUser() throws ResourceNotFoundException {
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail("aikins.dwamena@turntabl.io");

        doReturn(Optional.of(testItemsList)).when(itemRepository).findAllByUser(testUser);
        List<Item> actualItems = itemService.returnAllItemsByUser(jwtAuthenticationToken);

        verify(userRepository, times(1)).findByEmail("aikins.dwamena@turntabl.io");
        verify(itemRepository, times(1)).findAllByUser(testUser);
        Assertions.assertNotNull(actualItems);
    }

    @Test
    void testThat_givenAValidToken_addingAnItemShouldReturnString_itemAddedSuccessfully() throws ResourceNotFoundException {
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail("aikins.dwamena@turntabl.io");
        doReturn(Optional.of(testItem1)).when(itemRepository).save(testItem1);
        String expected = "item successfully added";
        String actual = itemService.addItem(new ItemRequest("aiks", "ss", ItemCondition.USED, Category.BOOKS, List.of()), jwtAuthenticationToken);
        verify(userRepository, times(1)).findByEmail("aikins.dwamena@turntabl.io");
        verify(itemRepository, times(1)).save(any());
        Assertions.assertEquals(expected, actual);

    }

    @Test
    void testThat_givenAValidToken_addingAnItem_shouldReturnAStatus404() {
        doReturn(Optional.empty()).when(userRepository).findByEmail(any());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.addItem(any(), jwtAuthenticationToken));
    }

    @Test
    void testThat_givenAValidToken_activeUserShouldBeAbleToDeleteOneOfItsDraftItems() throws ResourceNotFoundException, MismatchedEmailException, ItemAlreadyOnAuctionException {
        doReturn(Optional.of(testItem)).when(itemRepository).findById(any());
        String expected = "item deleted successfully";
        String actualResponse = itemService.deleteItem(any(), jwtAuthenticationToken);
        verify(itemRepository, times(1)).delete(any());
        Assertions.assertEquals(expected, actualResponse);
    }

    @Test
    void testThat_givenAValidToken_activeUserShouldNotBeAbleToDeleteOneOfItsDraftItemsOnAuction()  {
        doReturn(Optional.of(testItemOnAuction)).when(itemRepository).findById(any());

        Assertions.assertThrows(ItemAlreadyOnAuctionException.class, () -> itemService.deleteItem(any(), jwtAuthenticationToken));


    }

    @Test
    void testThat_givenAValidToken_activeUserShouldBeAbleToReturnOneOfItsItems() throws ResourceNotFoundException, MismatchedEmailException {
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail("aikins.dwamena@turntabl.io");
        doReturn(Optional.of(testItem1)).when(itemRepository).findById(any());
        Item item = itemService.returnOneItemOfUser(any(), jwtAuthenticationToken);

        Assertions.assertNotNull(item);
    }

    @Test
    void testThat_givenAValidToken_activeUserShouldExistInTheDatabase() {
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail("emmanuel@gmail.com");
        doReturn(Optional.empty()).when(itemRepository).findById(any());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.returnOneItemOfUser(any(), jwtAuthenticationToken));

    }

    @Test
    void testThat_givenAValidToken_activeUserShouldNotBeAbleAccessDifferentUserItem()  {
        Item testItem1 = new Item("test1", "test1", testUser1, null, true, true);

        doReturn(Optional.of(testItem1)).when(itemRepository).findById(any());

        Assertions.assertThrows(MismatchedEmailException.class, () -> itemService.returnOneItemOfUser(this.testItem1.getId(), jwtAuthenticationToken));

    }


    //check for 404 exception when finding user with no items


    @Test
    void returnAllAuctionItemsByUser_givenJwtAuthTokenAndUserHasNoItems_shouldThrowError() {
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail(any());
        doReturn(Optional.empty()).when(itemRepository).findAllByUser(testUser);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.returnAllAuctionItemsByUser(jwtAuthenticationToken));
    }

    //check for 404 exception when finding user with no auction items


    @Test
    void returnAllAuctionItemsByUser_givenJwtAuthTokenAndUserHasNoAuctionItems_shouldThrowError() {
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail(any());
        doReturn(Optional.of(testItemList2)).when(itemRepository).findAllByUser(testUser);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.returnAllAuctionItemsByUser(jwtAuthenticationToken));
    }

    //check for sweet part

    @Test
    void returnAllAuctionItemsByUser_givenJwtAuthToken_shouldReturnAllAuctionItemsByUser() throws ResourceNotFoundException {
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail("aikins.dwamena@turntabl.io");

        doReturn(Optional.of(testItemList)).when(itemRepository).findAllByUser(testUser);
        itemService.returnAllAuctionItemsByUser(jwtAuthenticationToken);

        verify(userRepository, times(1)).findByEmail("aikins.dwamena@turntabl.io");
        verify(itemRepository, times(1)).findAllByUser(testUser);
        Assertions.assertNotEquals(null, itemService.returnAllAuctionItemsByUser(jwtAuthenticationToken));
    }

    @Test
    void deleteItemOnAuction_givenAppropriateIdAndNoBidAvailable_shouldDeleteItem() throws MismatchedEmailException, ForbiddenActionException, ResourceNotFoundException {
        doReturn(Optional.of(testAuctionList)).when(auctionRepository).findAllByItemId(any());
        doReturn(Optional.empty()).when(bidRepository).findByAuction(any());

        itemService.deleteItemOnAuction(1L,jwtAuthenticationToken);

        verify(auctionRepository,times(1)).findAllByItemId(any());
        verify(bidRepository,times(1)).findByAuction(any());
        verify(itemRepository,times(1)).deleteById(any());
        Assertions.assertEquals("Item successfully deleted",itemService.deleteItemOnAuction(1L,jwtAuthenticationToken));
    }



    @Test
    void deleteItemOnAuction_givenItemNotOnAuction_shouldThrowResourceNotFoundException(){
        doReturn(Optional.empty()).when(auctionRepository)
                .findAllByItemId(any());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.deleteItemOnAuction(1L,jwtAuthenticationToken));
    }

    @Test
    void deleteItemOnAuction_givenLiveBid_shouldThrowForbiddenActionException(){
        doReturn(Optional.of(testAuctionList)).when(auctionRepository)
                .findAllByItemId(any());
        doReturn(Optional.of(testBidList)).when(bidRepository)
                .findByAuction(any());

        Assertions.assertThrows(ForbiddenActionException.class, ()-> itemService.deleteItemOnAuction(1L,jwtAuthenticationToken));
    }

    @Test
    void deleteItemOnAuction_givenAppropriateIdAndNoLiveAuction_shouldDeleteItem() throws MismatchedEmailException, ForbiddenActionException, ResourceNotFoundException {
        doReturn(Optional.of(testAuctionList2)).when(auctionRepository).findAllByItemId(any());


        itemService.deleteItemOnAuction(1L,jwtAuthenticationToken);

        verify(auctionRepository,times(1)).findAllByItemId(any());
        verify(bidRepository,times(0)).findByAuction(any());
        Assertions.assertEquals("Item successfully deleted",itemService.deleteItemOnAuction(1L,jwtAuthenticationToken));
    }

    @Test
    void deleteItemOnAuction_givenAppropriateIdAndButFalseToken_shouldThrowEmailMismatchException() throws MismatchedEmailException, ForbiddenActionException, ResourceNotFoundException {
        doReturn(Optional.of(testAuctionList1)).when(auctionRepository).findAllByItemId(any());
        doReturn(Optional.empty()).when(bidRepository).findByAuction(any());

        Assertions.assertThrows(MismatchedEmailException.class, () -> itemService.deleteItemOnAuction(1L,jwtAuthenticationToken));
    }




}