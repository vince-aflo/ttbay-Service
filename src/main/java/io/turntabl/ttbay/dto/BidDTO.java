package io.turntabl.ttbay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BidDTO(@NotBlank(message = "An amount needs to be entered") Double bidAmount,
                     @NotNull(message = "auction id needs to be set") Long auctionId) {

}
