package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.enums.AuctionStatus;
import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.Bid;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.BidRepository;
import io.turntabl.ttbay.service.GmailService;
import io.turntabl.ttbay.service.ThymeleafService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailTriggerServiceImplTest{
    private final User testUser1 = User.builder().username("aikscode").email("aikins.dwamena@turntabl.io").fullName("Aikins Akenten Dwamena").build();
    private final User testUser2 = User.builder().username("tkay").email("emmanuel.tweneboah@turntabl.io").fullName("Emmanuel Tweneboah Koduah").build();
    private final User testUser3 = User.builder().username("vincent").email("vincent.aflo@turntabl.io").fullName("Vincent Aflo").build();
    private final Item testItem = Item.builder().name("Book1").user(testUser3).onAuction(false).build();
    private final Auction testAuction = Auction.builder().id(1L).auctioner(testUser3).item(testItem).startDate(new Date()).endDate(new Date()).reservedPrice(81.5).status(AuctionStatus.LIVE).build();
    private final List<Bid> testBids = List.of(
            Bid.builder().bidAmount(1000.00).bidder(testUser1).id(1L).auction(testAuction).build(),
            Bid.builder().bidAmount(1500.00).bidder(testUser2).id(2L).auction(testAuction).build()
    );
    @Mock
    private BidRepository bidRepository;
    @Mock
    private ThymeleafService thymeleafService;
    @Mock
    private GmailService gmailService;
    @InjectMocks
    private EmailTriggerServiceImpl emailTriggerService;

    @BeforeEach
    void setUp(){
    }

    @Test
    void sendBidWasMadeEmail_givenAuctionUserBidAmount_shouldSend() throws MessagingException{
        doReturn(testBids).when(bidRepository).findByAuction(testAuction);
        doReturn(new Context()).when(thymeleafService).setTemplateContext(any());
        doReturn("").when(thymeleafService).createHtmlBody(any(), any());
        emailTriggerService.sendBidWasMadeEmail(testAuction, testUser2, 5000.00);
        verify(gmailService, times(2)).sendHtmlMessage((String) any(), any(), any());
    }
}