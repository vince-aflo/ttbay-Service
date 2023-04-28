package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.ItemRequest;
import io.turntabl.ttbay.dto.ItemResponseDTO;
import io.turntabl.ttbay.enums.AuctionStatus;
import io.turntabl.ttbay.enums.OfficeLocation;
import io.turntabl.ttbay.exceptions.ForbiddenActionException;
import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.*;
import io.turntabl.ttbay.service.ItemMapperService;
import io.turntabl.ttbay.service.ItemService;
import io.turntabl.ttbay.service.TokenAttributesExtractor;
import io.turntabl.ttbay.utils.mappers.ItemMapper;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static io.turntabl.ttbay.enums.Category.BOOKS;
import static io.turntabl.ttbay.enums.ItemCondition.NEW;
import static io.turntabl.ttbay.enums.ItemCondition.USED;
import static org.mockito.Mockito.*;

@SpringBootTest
class ItemServiceImplTest{
    private final Long id = 1L;
    private final User testUser = User.builder().username("aiks").email("aikins.dwamena@turntabl.io").fullName("Aikins Akenten Dwamena").officeLocation(OfficeLocation.SONNIDOM_HOUSE).build();
    private final User imposterUser = User.builder().username("Emma").email("a@gmail.com").fullName("Emmanuel Koduah Tweneboah").profileUrl("").officeLocation(OfficeLocation.SONNIDOM_HOUSE).build();
    private final Item testItem = Item.builder().name("Harry Potter").description("The Philosopher's stone").user(testUser).isSold(false).onAuction(false).build();
    private final Item testItem3 = Item.builder().name("Harry Potter").description("Chamber Of Secrets").user(testUser).isSold(true).onAuction(true).build();
    private final Item testItem2 = Item.builder().name("Harry Potter").description("Chamber Of Secrets").user(imposterUser).isSold(false).onAuction(false).build();
    private final List<Item> testItemsList = List.of(
            testItem,
            testItem3,
            Item.builder().name("Harry Potter").description("Prisoner of Azkaban").user(testUser).isSold(false).onAuction(false).build(),
            Item.builder().name("The Art of Attack").description("Guide to attacking in chess").user(testUser).isSold(true).onAuction(false).build()
    );
    private final List<Auction> testAuctionList = List.of(
            Auction.builder().id(id).auctioner(testUser).item(testItem3).startDate(new Date()).endDate(new Date()).reservedPrice(5000.0).currentHighestBid(6000.0).status(AuctionStatus.LIVE).build()
    );
    private final List<Auction> testAuctionList2 = List.of(
            Auction.builder().id(id).auctioner(testUser).item(testItem3).startDate(new Date()).endDate(new Date()).reservedPrice(5000.0).currentHighestBid(6000.0).status(AuctionStatus.SCHEDULED).build()

    );
    private final ItemResponseDTO responseDTO = new ItemResponseDTO(id, "aikins.dwamena@turntabl.io", "BOok", "This is a good book", false, false, USED, BOOKS, List.of(), List.of(), false, false, false,List.of());
    private final List<Auction> testAuctionList1 = List.of(
            Auction.builder().id(id).auctioner(imposterUser).item(testItem3).startDate(new Date()).endDate(new Date()).reservedPrice(5000.0).currentHighestBid(6000.0).status(AuctionStatus.LIVE).build()
    );
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
    @MockBean
    private ItemMapperService itemMapperService;
    @MockBean
    private ItemMapper itemMapper;
    @MockBean
    private ItemImageRepository itemImageRepository;
    @MockBean
    private AuctionRepository auctionRepository;
    @MockBean
    private BidRepository bidRepository;

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

    //check for 404 exception when finding user :
    @Test
    void returnAllAuctionItemsByUser_givenJwtAuthTokenAndNoUserInDb_shouldThrowError(){
        doReturn(Optional.empty()).when(userRepository).findByEmail(any());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.returnAllAuctionItemsByUser(jwtAuthenticationToken));
    }

    @Test
    void returnAllItemsByUser_givenJwtAuthTokenAndNoUserInDb_shouldThrowError() {
        doReturn(Optional.empty()).when(userRepository).findByEmail(any());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.returnAllItemsByUser(jwtAuthenticationToken));
    }

    @Test
    void returnAllItemsByUser_givenJwtAuthToken_shouldReturnAllItemsByUser() throws ResourceNotFoundException{
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail("aikins.dwamena@turntabl.io");
        doReturn(testItemsList).when(itemRepository).findByUser(testUser);
        List<ItemResponseDTO> actualItems = itemService.returnAllItemsByUser(jwtAuthenticationToken);
        verify(userRepository, times(1)).findByEmail("aikins.dwamena@turntabl.io");
        verify(itemRepository, times(1)).findByUser(testUser);
        Assertions.assertNotNull(actualItems);
    }

    @Test
    void addItem_givenAValidToken_shouldReturnStringItemAddedSuccessfully() throws ResourceNotFoundException{
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail("aikins.dwamena@turntabl.io");
        doReturn(responseDTO).when(itemMapperService).returnItemResponse(any());
        itemService.addItem(new ItemRequest("name", "test", NEW, BOOKS, List.of(),List.of()), jwtAuthenticationToken);
        verify(userRepository, times(1)).findByEmail("aikins.dwamena@turntabl.io");
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void updateItem_givenAValidToken_shouldUpdateactiveUsersItems() throws ResourceNotFoundException,MismatchedEmailException{
        doReturn(Optional.of(testItem)).when(itemRepository).findById(any());
        doNothing().when(itemImageRepository).deleteByItem(testItem);
        doReturn(new Item()).when(itemMapper).itemDTOtoItem(any(), any());
        String expected = "item updated successfully";
        String actualResponse = itemService.updateItem(any(), new ItemRequest("aiks", "ss", USED, BOOKS, List.of(),List.of()), jwtAuthenticationToken);
        verify(itemImageRepository, times(1)).deleteByItem(any());
        verify(itemRepository, times(1)).save(any());
        Assertions.assertEquals(expected, actualResponse);
    }

    @Test
    void addItem_givenAValidToken_shouldThrowResourceNotFoundError(){
        doReturn(Optional.empty()).when(userRepository).findByEmail(any());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.addItem(any(), jwtAuthenticationToken));
    }

    @Test
    void deleteDraftItem_givenAValidToken_shouldDeleteDraftItem() throws ResourceNotFoundException,MismatchedEmailException{
        doReturn(Optional.of(testItem)).when(itemRepository).findById(any());
        String expected = "item deleted successfully";
        String actualResponse = itemService.deleteDraftItem(any(), jwtAuthenticationToken);
        verify(itemRepository, times(1)).delete(any());
        Assertions.assertEquals(expected, actualResponse);
    }

    @Test
    void returnOneItemOfUser_givenAValidToken_shouldReturnOneOfUsersItems() throws ResourceNotFoundException,MismatchedEmailException{
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail("aikins.dwamena@turntabl.io");
        doReturn(Optional.of(testItem3)).when(itemRepository).findById(any());
        Item item = itemService.returnOneItem(jwtAuthenticationToken, any());
        itemService.returnOneItemOfUser(any(), jwtAuthenticationToken);
        Assertions.assertNotNull(item);
    }

    @Test
    void returnOneItemOfUser_givenAValidToken_shouldNotBeFoundInTheDatabase(){
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail("emmanuel@gmail.com");
        doReturn(Optional.empty()).when(itemRepository).findById(any());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.returnOneItemOfUser(any(), jwtAuthenticationToken));
    }

    @Test
    void returnOneItemOfUser_givenAInvalidToken_shouldNotBeAbleAccessDifferentUserItem(){
        Item testItem1 = new Item("test1", "test1", imposterUser, null, true, true);
        doReturn(Optional.of(testItem1)).when(itemRepository).findById(any());
        Assertions.assertThrows(MismatchedEmailException.class, () -> itemService.returnOneItemOfUser(this.testItem3.getId(), jwtAuthenticationToken));
    }

    @Test
    void returnOneItem_givenAnItemId_shouldReturnOneItemWithItemIdGiven() throws MismatchedEmailException,ResourceNotFoundException{
        doReturn(Optional.of(testItem)).when(itemRepository).findById(any());
        itemService.returnOneItem(jwtAuthenticationToken, id);
        verify(itemRepository, times(1)).findById(id);
        Assertions.assertEquals(testItem, itemService.returnOneItem(jwtAuthenticationToken, id));
    }

    @Test
    void returnOneItem_givenAnItemIdWithInvalidToken_shouldThrowMismatchedEmailException(){
        doReturn(Optional.of(testItem2)).when(itemRepository).findById(any());
        Assertions.assertThrows(MismatchedEmailException.class, () ->
                itemService.returnOneItem(jwtAuthenticationToken, id)
        );
    }

    @Test
    void returnOneItem_givenAnItemIdWithValidToken_shouldThrowResourceNotFoundException(){
        doReturn(Optional.empty()).when(itemRepository).findById(any());
        Assertions.assertThrows(ResourceNotFoundException.class, () ->
                itemService.returnOneItem(jwtAuthenticationToken, id)
        );
    }

    @Test
    void returnAllAuctionItemsByUser_givenJwtAuthToken_shouldReturnAllAuctionItemsByUser() throws ResourceNotFoundException{
        doReturn(Optional.of(testUser)).when(userRepository).findByEmail("aikins.dwamena@turntabl.io");
        itemService.returnAllAuctionItemsByUser(jwtAuthenticationToken);
        verify(userRepository, times(1)).findByEmail("aikins.dwamena@turntabl.io");
        verify(itemRepository, times(1)).findByUser(testUser);
        Assertions.assertNotEquals(null, itemService.returnAllAuctionItemsByUser(jwtAuthenticationToken));
    }

    @Test
    void deleteItemOnAuction_givenAppropriateIdAndNoBidAvailable_shouldDeleteItem() throws MismatchedEmailException, ForbiddenActionException, ResourceNotFoundException{
        doReturn(testAuctionList).when(auctionRepository).findByItem(any());
        doReturn(List.of()).when(bidRepository).findByAuction(any());
        doReturn(Optional.of(testItem)).when(itemRepository).findById(any());
        itemService.deleteItemOnAuction(id, jwtAuthenticationToken);
        verify(auctionRepository, times(1)).findByItem(any());
        verify(bidRepository, times(1)).findByAuction(any());
        verify(itemRepository, times(1)).deleteById(any());
        Assertions.assertEquals("Item successfully deleted", itemService.deleteItemOnAuction(id, jwtAuthenticationToken));
    }


    @Test
    void deleteItemOnAuction_givenItemNotOnAuction_shouldThrowResourceNotFoundException(){
        doReturn(List.of()).when(auctionRepository)
                .findByItem(any());
        Assertions.assertThrows(ResourceNotFoundException.class, () -> itemService.deleteItemOnAuction(id, jwtAuthenticationToken));
    }

    @Test
    void deleteItemOnAuction_givenAppropriateIdAndNoLiveAuction_shouldDeleteItem() throws MismatchedEmailException, ForbiddenActionException, ResourceNotFoundException{
        doReturn(testAuctionList2).when(auctionRepository).findByItem(any());
        doReturn(Optional.of(testItem)).when(itemRepository).findById(any());
        itemService.deleteItemOnAuction(id, jwtAuthenticationToken);
        verify(auctionRepository, times(1)).findByItem(any());
        verify(bidRepository, times(0)).findByAuction(any());
        Assertions.assertEquals("Item successfully deleted", itemService.deleteItemOnAuction(id, jwtAuthenticationToken));
    }

    @Test
    void deleteItemOnAuction_givenAppropriateIdAndButFalseToken_shouldThrowEmailMismatchException(){
        doReturn(testAuctionList1).when(auctionRepository).findByItem(any());
        doReturn(List.of()).when(bidRepository).findByAuction(any());
        doReturn(Optional.of(testItem)).when(itemRepository).findById(any());
        Assertions.assertThrows(MismatchedEmailException.class, () -> itemService.deleteItemOnAuction(id, jwtAuthenticationToken));
    }

    @Test
    void persistExchangedItemsInDb_givenEmptyItems_shouldReturnExecutionExceptions(){
        doReturn(List.of()).when(itemRepository).findAll();
        Assertions.assertThrows(ExecutionException.class, () -> itemService.persistExchangedItemsInDb().get());
    }

    @Test
    void persistExchangedItemsInDb_givenItems_shouldReturnSetItemsAsExchanged() throws ResourceNotFoundException, ExecutionException, InterruptedException{
        testItem.setHighestBidderReceivedItem(true);
        testItem.setAuctioneerHandItemToHighestBidder(true);
        testItem3.setHighestBidderReceivedItem(true);
        testItem3.setAuctioneerHandItemToHighestBidder(true);
        doReturn(List.of(testItem, testItem3, testItem2)).when(itemRepository).findAll();
        CompletableFuture<Void> future = itemService.persistExchangedItemsInDb();
        future.get();
        Assertions.assertTrue(testItem.isItemExchanged());
        Assertions.assertTrue(testItem3.isItemExchanged());
    }
}
