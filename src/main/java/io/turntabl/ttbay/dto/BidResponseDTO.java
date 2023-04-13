package io.turntabl.ttbay.dto;

import io.turntabl.ttbay.model.User;
import lombok.Builder;

@Builder
public record BidResponseDTO(
        Long bidId,
        Double bidAmount,
        User bidder,
        AuctionResponseDTO auctionResponseDTO
) {
}
