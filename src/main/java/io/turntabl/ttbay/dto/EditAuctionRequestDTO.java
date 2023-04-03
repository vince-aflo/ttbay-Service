package io.turntabl.ttbay.dto;
import java.util.Date;
public record EditAuctionRequestDTO(
     Long auctionId,
     Double reservedPrice,
     Date endDate
) {}
