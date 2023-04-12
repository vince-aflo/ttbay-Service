package io.turntabl.ttbay.controller;

import io.turntabl.ttbay.dto.BidDTO;
import io.turntabl.ttbay.exceptions.*;
import io.turntabl.ttbay.service.BidService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
@RequestMapping("api/v1/bid")
public class BidController {
    private final BidService bidService;

    @PostMapping("")
    public ResponseEntity<String> makeBid(@RequestBody BidDTO bidDTO , Authentication authentication) throws BidLessThanMaxBidException, ResourceNotFoundException, BidCannotBeZero, UserCannotBidOnTheirAuction, MessagingException, ForbiddenActionException {
        return ResponseEntity.status(HttpStatus.OK).body(bidService.makeBid(bidDTO,authentication));
    }
}
