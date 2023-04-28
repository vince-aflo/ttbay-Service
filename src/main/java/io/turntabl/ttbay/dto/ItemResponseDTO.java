package io.turntabl.ttbay.dto;

import io.turntabl.ttbay.enums.Category;
import io.turntabl.ttbay.enums.ItemCondition;
import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.ItemImage;
import io.turntabl.ttbay.model.Tag;
import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record ItemResponseDTO(
        Long itemId,
        String userEmail,
        String itemName,
        String description,
        Boolean onAuction,
        Boolean isSold,
        ItemCondition condition,
        Category category,
        List<ItemImage> imageList,
        List<Auction> auctions,
        boolean itemExchanged,
        boolean highestBidderReceivedItem,
        boolean auctioneerHandItemToHighestBidder,
        List<Tag> tags
) {}
