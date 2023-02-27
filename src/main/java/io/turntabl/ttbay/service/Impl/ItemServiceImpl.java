package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.ItemRepository;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    @Override
    public List<Item> returnAllAuctionItemsByUser(Authentication authentication) throws ResourceNotFoundException {

        // extract email
        JwtAuthenticationToken auth = (JwtAuthenticationToken) authentication;
        Map<String, Object> claims = auth.getTokenAttributes();
        String userEmail = (String) claims.get("email");

        //find user
        Optional<User> targetUser = userRepository.findByEmail(userEmail);

        if(targetUser.isEmpty())
            throw new ResourceNotFoundException("User cannot be found");

        //refactor and replace with getAllUser service method in the future;
        //Find all user items
        Optional<List<Item>> targetItems = itemRepository.findAllByUser(targetUser.get());

        if(targetItems.isEmpty())
            throw new ResourceNotFoundException("User currently has no items");

        //check and return items onAuction and not sold
        List<Item> onAuctionItems = targetItems.get().stream().filter(item -> item.getOnAuction() && !item.getIsSold()).toList();

        if(onAuctionItems.isEmpty())
            throw new ResourceNotFoundException("User has no items on auction");

        return onAuctionItems;
    }
}
