package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.ItemRequest;
import io.turntabl.ttbay.dto.ItemResponseDTO;
import io.turntabl.ttbay.enums.AuctionStatus;
import io.turntabl.ttbay.exceptions.*;
import io.turntabl.ttbay.model.*;
import io.turntabl.ttbay.repository.*;
import io.turntabl.ttbay.service.ItemMapperService;
import io.turntabl.ttbay.service.ItemService;
import io.turntabl.ttbay.service.TokenAttributesExtractor;
import io.turntabl.ttbay.utils.mappers.ItemMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final TokenAttributesExtractor tokenAttributesExtractor;
    private final UserRepository userRepository;
    private final ItemImageRepository itemImageRepository;
    private final ItemMapperService itemMapperService;

    @Override
    public List<ItemResponseDTO> returnAllAuctionItemsByUser(Authentication authentication) throws ResourceNotFoundException {
        List<ItemResponseDTO> allUserItems = returnAllItemsByUser(authentication);
        //check and return items onAuction and not sold
        return allUserItems.stream().filter(item -> item.onAuction() && !item.isSold()).toList();
    }


    @Override
    public ItemResponseDTO addItem(ItemRequest itemRequest, Authentication authentication) throws ResourceNotFoundException {
        String email = tokenAttributesExtractor.extractEmailFromToken(authentication);
        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Item newItem = Item.builder().user(currentUser).isSold(false).onAuction(false).condition(itemRequest.condition()).category(itemRequest.category()).name(itemRequest.name()).description(itemRequest.description()).build();
        newItem.setImageList(itemRequest.imageList().parallelStream().map(itemImage -> new ItemImage(newItem, itemImage.getImageUrl())).toList());
        try {
            Item item = itemRepository.save(newItem);
            return itemMapperService.returnItemResponse(item);
        } catch (Exception exception) {
            throw new ModelCreateException("Error creating item");
        }
    }

    @Override
    public ItemResponseDTO returnOneItemOfUser(Long itemId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException {
        Item item = returnOneItem(authentication, itemId);
        return itemMapperService.returnItemResponse(item);
    }

    @Override
    public String deleteDraftItem(Long itemId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException {
        Item item = returnOneItem(authentication, itemId);
        itemRepository.delete(item);
        return "item deleted successfully";
    }

    @Override
    public String deleteItemOnAuction(Long itemId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException {
        String tokenEmail = tokenAttributesExtractor.extractEmailFromToken(authentication);
        Item targetItem = returnOneItem(authentication,itemId);
        //find live auction by item id
        List<Auction> listAuction = auctionRepository.findByItem(targetItem);
        if (!listAuction.isEmpty() &&!listAuction.get(0).getAuctioner().getEmail().equalsIgnoreCase(tokenEmail))
            throw new MismatchedEmailException("You don't have access to this action");
        List<Auction> targetAuction = listAuction.stream().filter(auction -> auction.getStatus() == AuctionStatus.LIVE).toList();
        if (!targetAuction.isEmpty()) {
            //find bid by auction and throw exception if any
            List<Bid> availableBids = bidRepository.findByAuction(targetAuction.get(0));
            if ( !availableBids.isEmpty())
                return "Item on auction has bid(s)";
        }
        //delete if there's no bid
        itemRepository.deleteById(itemId);
        return "Item successfully deleted";
    }

    @Override
    public List<ItemResponseDTO> returnAllItemsByUser(Authentication authentication) throws ResourceNotFoundException {
        String email = tokenAttributesExtractor.extractEmailFromToken(authentication);
        Optional<User> targetUser = userRepository.findByEmail(email);
        if (targetUser.isEmpty()) throw new ResourceNotFoundException("User cannot be found");
        List<Item> targetItems = itemRepository.findByUser(targetUser.get());
        return targetItems.stream().map(itemMapperService::returnItemResponse).toList();
    }

    @Transactional
    @Override
    public String updateItem(Long itemId, ItemRequest itemRequest, Authentication authentication) throws MismatchedEmailException, ResourceNotFoundException {
        Item item = returnOneItem(authentication, itemId);
        itemImageRepository.deleteByItem(item);
        Item copy = ItemMapper.INSTANCE.itemDTOtoItem(itemRequest, item);
        itemRepository.save(copy);
        return "item updated successfully";
    }

    public Item returnOneItem(Authentication authentication, Long itemId) throws ResourceNotFoundException, MismatchedEmailException {
        String email = tokenAttributesExtractor.extractEmailFromToken(authentication);
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent() && !Objects.equals(item.get().getUser().getEmail(), email)) {
            throw new MismatchedEmailException("You don't have access to this resource");
        } else if (item.isEmpty()) {
            throw new ResourceNotFoundException("Item not found");
        }
        return item.get();
    }
    @Scheduled(cron = "0 */5 * * * *")
    @Async
    public CompletableFuture<Void> persistExchangedItemsInDb() throws ResourceNotFoundException {
        List<Item> itemsNotExchanged = getAllItemsNotExchanged();

        itemsNotExchanged.parallelStream().filter(item -> item.isAuctioneerHandItemToHighestBidder() && item.isHighestBidderReceivedItem()).forEach(item -> {
            setItemAsExchanged(item);
            itemRepository.save(item);
        });

        return CompletableFuture.completedFuture(null);
    }
    private void setItemAsExchanged(Item item){
        item.setItemExchanged(true);
    }
    private List<Item> getAllItemsNotExchanged() throws ResourceNotFoundException {
        List<Item> allItems = itemRepository.findAll();
        if (allItems.isEmpty()){
            throw new ResourceNotFoundException("Empty items");
        }
        return allItems.parallelStream().filter(item -> !item.isItemExchanged()).toList();
    }
}
