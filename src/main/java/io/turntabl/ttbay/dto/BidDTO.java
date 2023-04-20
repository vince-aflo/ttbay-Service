package io.turntabl.ttbay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BidDTO(
        @NotBlank(message = "An amount needs to be entered") Double bidAmount,
         Long auctionId
){}
