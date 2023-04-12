package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.BidResponseDTO;
import io.turntabl.ttbay.model.Bid;
import io.turntabl.ttbay.service.AuctionMapperService;
import io.turntabl.ttbay.service.BidMapperService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BidMapperImpl implements BidMapperService {
    private final AuctionMapperService auctionMapperService;

    @Override
    public BidResponseDTO returnBidResponse(Bid bid) {
        return BidResponseDTO.builder().bidId(bid.getId()).bidAmount(bid.getBidAmount()).bidder(bid.getBidder()).auctionResponseDTO(auctionMapperService.returnAuctionResponse(bid.getAuction())).build();
    }
}
