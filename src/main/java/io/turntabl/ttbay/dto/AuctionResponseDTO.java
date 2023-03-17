package io.turntabl.ttbay.dto;

import io.turntabl.ttbay.enums.AuctionStatus;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.User;
import lombok.Builder;

import java.util.Date;

@Builder
public record AuctionResponseDTO(
        Long auctionId,
        String auctioneerEmail,
        Item item,
        Date startDate,
        Date endDate,
        Double reservedPrice,
        Double currentHighestBid,
        User winner,
        AuctionStatus status

) {}
