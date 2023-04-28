package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.ItemResponseDTO;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.service.ItemMapperService;
import org.springframework.stereotype.Service;

@Service
public class ItemMapperServiceImpl implements ItemMapperService{
    @Override
    public ItemResponseDTO returnItemResponse(Item item){
        return ItemResponseDTO.builder().itemId(item.getId()).userEmail(item.getUser().getEmail()).itemName(item.getName()).description(item.getDescription()).onAuction(item.getOnAuction()).isSold(item.getIsSold()).condition(item.getCondition()).category(item.getCategory()).imageList(item.getImageList()).auctions(item.getAuction()).itemExchanged(item.isItemExchanged()).highestBidderReceivedItem(item.isHighestBidderReceivedItem()).auctioneerHandItemToHighestBidder(item.isAuctioneerHandItemToHighestBidder()).tags(item.getTags()).build();
    }
}
