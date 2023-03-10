package io.turntabl.ttbay.controller;


import io.turntabl.ttbay.dto.AuctionRequest;
import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.service.AuctionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/auctions")
public class AuctionController {
    private final AuctionService auctionService;

    @GetMapping("/all-by-user")
    public ResponseEntity<List<Auction>> returnAllAuction(Authentication authentication) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(auctionService.returnAllAuctionByUser(authentication));
    }
    @PostMapping("/add")
    public ResponseEntity<String> createAuction(@RequestBody AuctionRequest auctionRequest, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException {
        return ResponseEntity.status(HttpStatus.OK).body(auctionService.createAuction(auctionRequest,authentication));
    }
    @GetMapping("/{auctionId}")
    public ResponseEntity<Auction> getOneAuctionOfUser(@PathVariable Long auctionId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException {
        return ResponseEntity.status(HttpStatus.OK).body(auctionService.returnOneAuctionOfUser(auctionId, authentication));
    }
    @DeleteMapping("/{auctionId}")
    public ResponseEntity<String> deleteAuctionWithNoBId(@PathVariable Long auctionId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException {
        return ResponseEntity.status(HttpStatus.OK).body(auctionService.deleteAuctionWithNoBId(auctionId, authentication));
    }

}