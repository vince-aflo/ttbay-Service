package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.AuctionRequest;
import io.turntabl.ttbay.exceptions.ItemAlreadyOnAuctionException;
import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ModelCreateException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.AuctionRepository;
import io.turntabl.ttbay.repository.ItemRepository;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.AuctionService;
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
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final TokenAttributesExtractor tokenAttributesExtractor;
    private final ItemRepository itemRepository;

    @Override
    public List<Auction> returnAllAuctionByUser(Authentication authentication) throws ResourceNotFoundException {
        String email = tokenAttributesExtractor.extractEmailFromToken(authentication);
        User targetUser = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Optional<List<Auction>> allAuctions = auctionRepository.findAllByAuctioner(targetUser);
        if (allAuctions.isEmpty()) throw new ResourceNotFoundException("Empty auctions");
        return allAuctions.get();
    }
    @Override
    public Auction returnOneAuctionOfUser(Long itemId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException {
        String email = tokenAttributesExtractor.extractEmailFromToken(authentication);

        Optional<Auction> auction = auctionRepository.findById(itemId);
        if (auction.isPresent() && !Objects.equals(auction.get().getAuctioner().getEmail(), email)) {
            throw new MismatchedEmailException("You don't have access to this resource");
        } else if (auction.isEmpty()) {
            throw new ResourceNotFoundException("Item not found");
        }
        return auction.get();
    }
    @Override
    public String createAuction(AuctionRequest auctionRequest, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException {
        Item item = itemService.returnOneItemOfUser(auctionRequest.itemId(),authentication);

        try {
            if(item.getOnAuction()){
               throw new ItemAlreadyOnAuctionException("This is item is already on auction");
            }
            Auction newAuction = Auction.builder()
                    .reservedPrice(auctionRequest.price())
                    .auctioner(item.getUser()).item(item)
                    .startDate(auctionRequest.startDate())
                    .endDate(auctionRequest.endDate())
                    .status(auctionRequest.status())
                    .build();

            item.setOnAuction(true);
            itemRepository.save(item);
            auctionRepository.save(newAuction);
            return "Auction created successfully";
        } catch (Exception exception) {
            throw new ModelCreateException("Error creating models");
        }
    }
    @Override
    public String deleteAuctionWithNoBId(Long auctionId, Authentication authentication) throws ResourceNotFoundException {

        return null;
    }
}
