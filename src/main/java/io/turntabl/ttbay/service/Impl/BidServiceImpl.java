package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.BidDTO;
import io.turntabl.ttbay.exceptions.BidCannotBeZero;
import io.turntabl.ttbay.exceptions.BidLessThanMaxBidException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.exceptions.UserCannotBidOnTheirAuction;
import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.Bid;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.AuctionRepository;
import io.turntabl.ttbay.repository.BidRepository;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.BidService;
import io.turntabl.ttbay.service.TokenAttributesExtractor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;

    private final UserRepository userRepository;
    private final TokenAttributesExtractor tokenAttributesExtractor;
    @Override
    public String makeBid(BidDTO bidDTO, Authentication authentication) throws ResourceNotFoundException, BidLessThanMaxBidException, BidCannotBeZero, UserCannotBidOnTheirAuction {
        // first bid should be greater than 0
        if(bidDTO.bidAmount() <= 0) throw new BidCannotBeZero();

        //get bidder info else throw exception
        String email = tokenAttributesExtractor.extractEmailFromToken(authentication);
        Optional<User> bidder = userRepository.findByEmail(email);
        if(bidder.isEmpty()) throw new ResourceNotFoundException("User cannot be found");


        //fetch auction by id
        Optional<Auction> auction = auctionRepository.findById(bidDTO.auctionId());
        if(auction.isEmpty())
            throw new ResourceNotFoundException("Auction could not be found");

        //auctioneer shouldn't be able to bid on his/her own item
        if(auction.get().getAuctioner().getEmail().equalsIgnoreCase(email) )throw new UserCannotBidOnTheirAuction();

        //fetch AllBids by AuctionId and use stream to get max amount
        List<Bid> bidsOfTargetAuction = bidRepository.findByAuction(auction.get());

        if(!bidsOfTargetAuction.isEmpty()){

            //check from dto if bidAmount is less than the max bid and throw exception
            double maxBid = bidsOfTargetAuction.stream().mapToDouble(Bid::getBidAmount).max().orElse(0.0);
            if(maxBid > bidDTO.bidAmount()) throw new BidLessThanMaxBidException("Bid is less than current maximum bid");

        }


        //else save bid with builder
        Bid newBid = Bid.builder().bidAmount(bidDTO.bidAmount())
                .bidder(bidder.get())
                .auction(auction.get())
                .build();

        bidRepository.save(newBid);

        return "Bid has been made successfully";

    }
}
