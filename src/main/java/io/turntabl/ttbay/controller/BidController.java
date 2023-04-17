package io.turntabl.ttbay.controller;

import io.turntabl.ttbay.dto.BidDTO;
import io.turntabl.ttbay.dto.BidResponseDTO;
import io.turntabl.ttbay.exceptions.*;
import io.turntabl.ttbay.service.BidService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("api/v1/bids")
public class BidController{
    private final BidService bidService;

    @PostMapping("")
    public ResponseEntity<String> makeBid(@RequestBody BidDTO bidDTO , Authentication authentication) throws BidLessThanMaxBidException, ResourceNotFoundException, BidCannotBeZero, UserCannotBidOnTheirAuction, MessagingException, ForbiddenActionException{
        return ResponseEntity.status(HttpStatus.OK).body(bidService.makeBid(bidDTO,authentication));
    }

    @GetMapping("/all-by-user")
    public ResponseEntity<List<BidResponseDTO>> returnAllBids(Authentication authentication) throws ResourceNotFoundException{
        return ResponseEntity.status(HttpStatus.OK).body(bidService.returnAllBidsByUser(authentication));
    }

    @GetMapping("/bid-count/{auctionId}")
    public ResponseEntity<Long> getBidCount(@PathVariable Long auctionId) throws ResourceNotFoundException{
        return ResponseEntity.status(HttpStatus.OK).body(bidService.getBidCount(auctionId));
    }
}
