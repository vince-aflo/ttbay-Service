package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.AuctionResponseDTO;
import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.service.AuctionMapperService;
import io.turntabl.ttbay.service.ItemMapperService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class AuctionMapperImpl implements AuctionMapperService {
    private final ItemMapperService itemMapperService;
    @Override
    public AuctionResponseDTO returnAuctionResponse(Auction auction) {
        return AuctionResponseDTO.builder().auctionId(auction.getId()).auctioneerEmail(auction.getAuctioner().getEmail()).item(itemMapperService.returnItemResponse(auction.getItem())).startDate(auction.getStartDate()).endDate(auction.getEndDate()).reservedPrice(auction.getReservedPrice()).currentHighestBid(auction.getCurrentHighestBid()).winner(auction.getWinner()).status(auction.getStatus()).build();
    }
}
