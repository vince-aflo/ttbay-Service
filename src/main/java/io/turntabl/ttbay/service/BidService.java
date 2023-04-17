package io.turntabl.ttbay.service;

import io.turntabl.ttbay.dto.BidDTO;
import io.turntabl.ttbay.dto.BidResponseDTO;
import io.turntabl.ttbay.exceptions.*;
import org.springframework.security.core.Authentication;
import jakarta.mail.MessagingException;
import java.util.List;

public interface BidService{
    String makeBid(BidDTO bidDTO, Authentication authentication) throws ResourceNotFoundException, BidLessThanMaxBidException, BidCannotBeZero, UserCannotBidOnTheirAuction,MessagingException, ForbiddenActionException;
    List<BidResponseDTO> returnAllBidsByUser(Authentication authentication) throws ResourceNotFoundException;
    Long getBidCount(Long auctionId) throws ResourceNotFoundException;
}
