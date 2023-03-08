package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.ItemRequest;
import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ModelCreateException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.ItemImage;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.ItemRepository;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.ItemService;
import io.turntabl.ttbay.service.TokenAttributesExtractor;
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
    private final TokenAttributesExtractor tokenAttributesExtractor;
    private final UserRepository userRepository;

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
    public List<Item> returnAllItemsByUser(Authentication authentication) throws ResourceNotFoundException {
        String email = tokenAttributesExtractor.extractEmailFromToken(authentication);

        Optional<User> targetUser = userRepository.findByEmail(email);

        if (targetUser.isEmpty()) throw new ResourceNotFoundException("User cannot be found");

        Optional<List<Item>> targetItems = itemRepository.findAllByUser(targetUser.get());

        if (targetItems.isEmpty()) throw new ResourceNotFoundException("User currently has no items");

        return targetItems.get();
    }
}
