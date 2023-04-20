package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.Bid;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.BidRepository;
import io.turntabl.ttbay.service.EmailTriggerService;
import io.turntabl.ttbay.service.GmailService;
import io.turntabl.ttbay.service.ThymeleafService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class EmailTriggerServiceImpl implements EmailTriggerService{
    private final BidRepository bidRepository;
    private final ThymeleafService thymeleafService;
    private final GmailService gmailService;

    @Async
    @Override
    public void sendBidWasMadeEmail(Auction auction, User bidder, Double bidAmount) throws MessagingException{
        //create an array of strings of recipients
        List<Bid> listOfAllBidsOnTargetAuction = bidRepository.findByAuction(auction);
        List<User> listOfAllBidders = listOfAllBidsOnTargetAuction.stream().map(Bid::getBidder).toList();
        User[] arrayOfAllBidders = listOfAllBidders.toArray(new User[0]);
        for (User arrayOfAllBidder : arrayOfAllBidders) {
            //create map and context
            Map<String, Object> mapForContext= new HashMap<>();
            mapForContext.put("username", arrayOfAllBidder.getUsername());
            mapForContext.put("itemName", auction.getItem().getName());
            mapForContext.put("bidAmount", bidAmount);
            mapForContext.put("bidderUsername", bidder.getUsername());
            Context context = thymeleafService.setTemplateContext(mapForContext);
            //create htmlbody
            String htmlBody = thymeleafService.createHtmlBody(context,"bid-was-made.html");
            //send email to bidders of auction
            gmailService.sendHtmlMessage(arrayOfAllBidder.getEmail(),"Bid Was Made", htmlBody);
        }
    }

    @Override
    @Async
    public void sendAuctioneerAfterHighestWinEmail(Auction auction) throws MessagingException{
        Map<String, Object> mapForContext= new HashMap<>();
        mapForContext.put("username",auction.getAuctioner().getUsername());
        mapForContext.put("highestBidderEmail", auction.getWinner().getEmail());
        mapForContext.put("itemName", auction.getItem().getName());
        mapForContext.put("bidAmount", auction.getWinningPrice());
        mapForContext.put("highestBidderUsername", auction.getWinner().getUsername());
        Context context = thymeleafService.setTemplateContext(mapForContext);
        String htmlBody = thymeleafService.createHtmlBody(context,"bid-winner.html");
        String subject = auction.getWinner().getUsername()+" won your auction with item"+ auction.getItem().getName();
        gmailService.sendHtmlMessage(auction.getWinner().getEmail(), subject,htmlBody);
    }

    @Async
    @Override
    public void sendBidWinnerEmail(Auction auction) throws MessagingException{
            //create map and context
            Map<String, Object> mapForContext= new HashMap<>();
            mapForContext.put("username", auction.getWinner().getUsername());
            mapForContext.put("auctioneerEmail", auction.getAuctioner().getEmail());
            mapForContext.put("itemName", auction.getItem().getName());
            mapForContext.put("bidAmount", auction.getWinningPrice());
            mapForContext.put("auctioneerUsername", auction.getAuctioner().getUsername());
            Context context = thymeleafService.setTemplateContext(mapForContext);
            //create htmlbody
            String htmlBody = thymeleafService.createHtmlBody(context,"bid-winner.html");
            //send email to bidders of auction
            gmailService.sendHtmlMessage(auction.getWinner().getEmail(),"You won !!!!!!",htmlBody);
    }
}
