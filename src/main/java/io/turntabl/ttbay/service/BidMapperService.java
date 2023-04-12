package io.turntabl.ttbay.service;
import io.turntabl.ttbay.dto.BidResponseDTO;
import io.turntabl.ttbay.model.Bid;

public interface BidMapperService {
    BidResponseDTO returnBidResponse(Bid bid);
}
