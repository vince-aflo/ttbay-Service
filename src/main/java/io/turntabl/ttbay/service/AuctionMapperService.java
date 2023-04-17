package io.turntabl.ttbay.service;

import io.turntabl.ttbay.dto.AuctionResponseDTO;
import io.turntabl.ttbay.model.Auction;

public interface AuctionMapperService{
    AuctionResponseDTO returnAuctionResponse(Auction auction);
}
