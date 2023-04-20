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
import java.util.concurrent.CompletableFuture;

public interface AuctionService{
    List<AuctionResponseDTO> returnAllAuctionByUser(Authentication authentication) throws ResourceNotFoundException;
    AuctionResponseDTO returnOneAuctionOfUser(Long auctionId) throws ResourceNotFoundException, MismatchedEmailException;
    Auction returnOneAuction(Long auctionId) throws ResourceNotFoundException, MismatchedEmailException;
    String createAuction(AuctionRequest auctionRequest, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException;
    CompletableFuture<Void> updateDraftAuctionToLiveAndPersistInDatabase() throws ResourceNotFoundException;
    AuctionResponseDTO updateAuctionWithNoBid(EditAuctionRequestDTO editAuctionRequestDTO, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException, ForbiddenActionException;
    CompletableFuture<Void> updateAuctionWithWinnerAndBidAmount() throws ResourceNotFoundException;
    CompletableFuture<Void> updateLiveAuctionToEndAndPersistInDatabase() throws ResourceNotFoundException;
    List<AuctionResponseDTO> returnAllAuctions() ;
    String cancelAuctionWithBidChecking(Long auctionId, Authentication authentication) throws MismatchedEmailException, ResourceNotFoundException;
    String cancelAuction(Long auctionId, Authentication authentication) throws MismatchedEmailException, ResourceNotFoundException;
    void updateCurrentHighestBidOfAuction(Auction auction, Double highestBid);
    String auctioneerMarkAuctionedItemAsDelivered(Long auctionId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException;
    String auctionWinnerMarkAuctionedItemAsReceived(Long auctionId, Authentication authentication) throws MismatchedEmailException, ResourceNotFoundException;
}
