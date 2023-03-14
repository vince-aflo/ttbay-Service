package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.ItemRequest;
import io.turntabl.ttbay.enums.AuctionStatus;
import io.turntabl.ttbay.exceptions.*;
import io.turntabl.ttbay.model.*;
import io.turntabl.ttbay.repository.*;
import io.turntabl.ttbay.service.ItemService;
import io.turntabl.ttbay.service.TokenAttributesExtractor;
import io.turntabl.ttbay.utils.mappers.ItemMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final AuctionRepository auctionRepository;

    private final BidRepository bidRepository;
    private final TokenAttributesExtractor tokenAttributesExtractor;
    private final UserRepository userRepository;
    private final ItemImageRepository itemImageRepository;

    @Override
    public List<Item> returnAllAuctionItemsByUser(Authentication authentication) throws ResourceNotFoundException {

        List<Item> allUserItems = returnAllItemsByUser(authentication);

        //check and return items onAuction and not sold
        List<Item> onAuctionItems = allUserItems.stream().filter(item -> item.getOnAuction() && !item.getIsSold()).toList();

        if (onAuctionItems.isEmpty()) throw new ResourceNotFoundException("User has no items on auction");

        return onAuctionItems;
    }


    @Override
    public String addItem(ItemRequest itemRequest, Authentication authentication) throws ResourceNotFoundException {
        String email = tokenAttributesExtractor.extractEmailFromToken(authentication);

        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Item newItem = Item.builder().user(currentUser).isSold(false).onAuction(false).condition(itemRequest.condition()).category(itemRequest.category()).name(itemRequest.name()).description(itemRequest.description()).build();
        newItem.setImageList(itemRequest.imageList().parallelStream().map(itemImage -> new ItemImage(newItem, itemImage.getImageUrl())).toList());
        try {
            itemRepository.save(newItem);
            return "item successfully added";
        } catch (Exception exception) {
            throw new ModelCreateException("Error creating item");
        }
    }

    @Override
    public Item returnOneItemOfUser(Long itemId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException {
        String email = tokenAttributesExtractor.extractEmailFromToken(authentication);

        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent() && !Objects.equals(item.get().getUser().getEmail(), email)) {
            throw new MismatchedEmailException("You don't have access to this resource");
        } else if (item.isEmpty()) {
            throw new ResourceNotFoundException("Item not found");
        }

        return item.get();
    }

    @Override
    public String deleteDraftItem(Long itemId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException, ItemAlreadyOnAuctionException {
        Item item = returnOneItemOfUser(itemId, authentication);
        if (item.getOnAuction()) throw new ItemAlreadyOnAuctionException("cannot remove item(draft) on auction");
        itemRepository.delete(item);

        return "item deleted successfully";
    }

    @Override
    public String deleteItemOnAuction(Long itemId, Authentication authentication) throws ResourceNotFoundException, ForbiddenActionException, MismatchedEmailException {
        String tokenEmail = tokenAttributesExtractor.extractEmailFromToken(authentication);


        //find live auction by item id
        Optional<List<Auction>> listAuction = auctionRepository.findAllByItemId(itemId);

        if (listAuction.isEmpty()) throw new ResourceNotFoundException("Item isn't on auction");
        if (!listAuction.get().get(0).getAuctioner().getEmail().equalsIgnoreCase(tokenEmail))
            throw new MismatchedEmailException("You don't have access to this action");

        Optional<List<Auction>> targetAuction = Optional.of(listAuction.get()
                .stream()
                .filter(auction -> auction.getStatus() == AuctionStatus.LIVE)
                .toList()
        );

        if (!targetAuction.get().isEmpty()) {
            //find bid by auction and throw exception if any
            Optional<List<Bid>> availableBids = bidRepository.findByAuction(targetAuction.get().get(0));
            if (availableBids.isPresent() && !availableBids.get().isEmpty()) throw new ForbiddenActionException("Item on auction has bid(s)");
        }

        //delete if there's no bid
        itemRepository.deleteById(itemId);

        return "Item successfully deleted";
    }

    @Override
    public List<Item> returnAllItemsByUser(Authentication authentication) throws ResourceNotFoundException {
        String email = tokenAttributesExtractor.extractEmailFromToken(authentication);

        Optional<User> targetUser = userRepository.findByEmail(email);

        if (targetUser.isEmpty()) throw new ResourceNotFoundException("User cannot be found");

        Optional<List<Item>> targetItems = itemRepository.findAllByUser(targetUser.get());

        if (targetItems.isEmpty()) throw new ResourceNotFoundException("User currently has no items");

        return targetItems.get();
    }

    @Transactional
    @Override
    public String updateItem(Long itemId, ItemRequest itemRequest, Authentication authentication) throws MismatchedEmailException, ResourceNotFoundException {
        Item item = returnOneItemOfUser(itemId, authentication);

        itemImageRepository.deleteByItem(item);

        Item copy = ItemMapper.INSTANCE.itemDTOtoProfile(itemRequest, item);

        itemRepository.save(copy);

        return "item updated successfully";
    }
}
