package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.AuctionResponseDTO;
import io.turntabl.ttbay.enums.AuctionStatus;
import io.turntabl.ttbay.enums.OfficeLocation;
import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.service.AuctionMapperService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class AuctionMapperImplTest {
    private final User testUser = new User(
            "aikscode",
            "aikins.dwamena@turntabl.io",
            "Aikins Akenten Dwamena",
            "",
            OfficeLocation.SONNIDOM_HOUSE);
    private final Item testItem = new Item("Book1", "Harry Potter2", testUser, null, false, false);
    private final Auction auction = new Auction(
            1L,
            testUser,
            testItem,
            new Date(),
            new Date(), 20.2, 21.5, null, AuctionStatus.LIVE
    );
    private final AuctionResponseDTO auctionResponseDTO = AuctionResponseDTO.builder().auctionId(auction.getId()).auctioneerEmail(auction.getAuctioner().getEmail()).item(auction.getItem()).startDate(auction.getStartDate()).endDate(auction.getEndDate()).reservedPrice(auction.getReservedPrice()).currentHighestBid(auction.getCurrentHighestBid()).winner(auction.getWinner()).status(auction.getStatus()).build();
    @Autowired
    private AuctionMapperService auctionMapperService;

    @Test
    void returnAuctionResponse() {
        AuctionResponseDTO actualDTO = auctionMapperService.returnAuctionResponse(auction);
        Assertions.assertEquals(auctionResponseDTO, actualDTO);
    }
}