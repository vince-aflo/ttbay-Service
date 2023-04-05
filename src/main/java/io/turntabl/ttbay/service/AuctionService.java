package io.turntabl.ttbay.service;

import io.turntabl.ttbay.dto.AuctionRequest;
import io.turntabl.ttbay.dto.AuctionResponseDTO;
import io.turntabl.ttbay.dto.EditAuctionRequestDTO;
import io.turntabl.ttbay.exceptions.ForbiddenActionException;
import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.Auction;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AuctionService {
    List<AuctionResponseDTO> returnAllAuctionByUser(Authentication authentication) throws ResourceNotFoundException;
    AuctionResponseDTO returnOneAuctionOfUser(Long auctionId) throws ResourceNotFoundException, MismatchedEmailException;
    Auction returnOneAuction(Long auctionId) throws ResourceNotFoundException, MismatchedEmailException;
    String createAuction(AuctionRequest auctionRequest, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException;
    String deleteAuctionWithNoBId(Long actionId, Authentication authentication) throws ResourceNotFoundException,MismatchedEmailException;
    void updateDraftAuctionToLiveAndPersistInDatabase() throws ResourceNotFoundException;
    AuctionResponseDTO updateAuctionWithNoBid(EditAuctionRequestDTO editAuctionRequestDTO, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException, ForbiddenActionException;
    List<AuctionResponseDTO> returnAllAuctions() ;
    void updateCurrentHighestBidOfAuction(Auction auction, Double highestBid);

}
