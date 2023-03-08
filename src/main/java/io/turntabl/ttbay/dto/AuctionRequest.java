package io.turntabl.ttbay.dto;

import io.turntabl.ttbay.enums.AuctionStatus;


import java.util.Date;

public record AuctionRequest(
    Long itemId,
    Date startDate,
    Date endDate,
    Double price,
    AuctionStatus status
 )
{}
