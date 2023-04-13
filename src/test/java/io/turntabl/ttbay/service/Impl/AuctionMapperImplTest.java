package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.AuctionResponseDTO;
import io.turntabl.ttbay.dto.ItemResponseDTO;
import io.turntabl.ttbay.enums.AuctionStatus;
import io.turntabl.ttbay.enums.Category;
import io.turntabl.ttbay.enums.ItemCondition;
import io.turntabl.ttbay.enums.OfficeLocation;
import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.service.ItemMapperService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AuctionMapperImplTest {
    private final User testUser = new User(
            "aikscode",
            "aikins.dwamena@turntabl.io",
            "Aikins Akenten Dwamena",
            "",
            OfficeLocation.SONNIDOM_HOUSE);
    private final ItemResponseDTO itemResponseDTO = new ItemResponseDTO(1L, testUser.getEmail(), "Book1",
            "This is a good read", false, false, ItemCondition.USED, Category.FASHION, List.of(), List.of(),false,false,false);
    private final Auction auction = Auction.builder().id(1L).auctioner(testUser).startDate(new Date()).endDate(new Date()).reservedPrice(20.5).currentHighestBid(21.5).status(AuctionStatus.LIVE).build();
    private final AuctionResponseDTO auctionResponseDTO = AuctionResponseDTO.builder().auctionId(auction.getId()).auctioneerEmail(auction.getAuctioner().getEmail()).item(itemResponseDTO).startDate(auction.getStartDate()).endDate(auction.getEndDate()).reservedPrice(auction.getReservedPrice()).currentHighestBid(auction.getCurrentHighestBid()).winner(auction.getWinner()).status(auction.getStatus()).build();
    @Mock
    private ItemMapperService itemMapperService;
    @InjectMocks
    private AuctionMapperImpl auctionMapperService;


    @Test
    void returnAuctionResponse() {
        Mockito.doReturn(itemResponseDTO).when(itemMapperService).returnItemResponse(any());
        AuctionResponseDTO actualDTO = auctionMapperService.returnAuctionResponse(auction);
        Assertions.assertEquals(auctionResponseDTO, actualDTO);
    }
}