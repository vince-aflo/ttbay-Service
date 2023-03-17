package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.AuctionResponseDTO;
import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.service.AuctionMapperService;
import org.springframework.stereotype.Service;

@Service
public class AuctionMapperImpl implements AuctionMapperService {
    @Override
    public AuctionResponseDTO returnAuctionResponse(Auction auction) {
        return AuctionResponseDTO.builder().auctionId(auction.getId()).auctioneerEmail(auction.getAuctioner().getEmail()).item(auction.getItem()).startDate(auction.getStartDate()).endDate(auction.getEndDate()).reservedPrice(auction.getReservedPrice()).currentHighestBid(auction.getCurrentHighestBid()).winner(auction.getWinner()).status(auction.getStatus()).build();
    }
}
