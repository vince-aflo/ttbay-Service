package io.turntabl.ttbay.service;

import io.turntabl.ttbay.dto.BidDTO;
import io.turntabl.ttbay.exceptions.*;
import org.springframework.security.core.Authentication;

public interface BidService {
    String makeBid(BidDTO bidDTO, Authentication authentication) throws ResourceNotFoundException, BidLessThanMaxBidException, BidCannotBeZero, UserCannotBidOnTheirAuction, ForbiddenActionException;
}
