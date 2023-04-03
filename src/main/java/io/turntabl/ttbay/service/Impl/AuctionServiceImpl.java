package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.AuctionRequest;
import io.turntabl.ttbay.dto.AuctionResponseDTO;
import io.turntabl.ttbay.dto.EditAuctionRequestDTO;
import io.turntabl.ttbay.exceptions.*;
import io.turntabl.ttbay.model.Auction;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.repository.AuctionRepository;
import io.turntabl.ttbay.repository.ItemRepository;
import io.turntabl.ttbay.repository.UserRepository;
import io.turntabl.ttbay.service.AuctionMapperService;
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

    private final AuctionMapperService auctionMapperService;

    @Override
    public List<AuctionResponseDTO> returnAllAuctionByUser(Authentication authentication) throws ResourceNotFoundException {
        String email = tokenAttributesExtractor.extractEmailFromToken(authentication);
        User targetUser = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Auction> allAuctions = auctionRepository.findByAuctioner(targetUser);
        return allAuctions.stream().map(auctionMapperService::returnAuctionResponse).toList();
    }

    @Override
    public AuctionResponseDTO returnOneAuctionOfUser(Long auctionId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException {
        Auction auction = returnOneAuction(auctionId, authentication);
        return auctionMapperService.returnAuctionResponse(auction);
    }

    @Override
    public String createAuction(AuctionRequest auctionRequest, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException {
        Item item = itemService.returnOneItem(authentication, auctionRequest.itemId());

        try {
            if (item.getOnAuction()) {
                throw new ItemAlreadyOnAuctionException("This is item is already on auction");
            }
            Auction newAuction = Auction.builder().reservedPrice(auctionRequest.price()).auctioner(item.getUser()).item(item).startDate(auctionRequest.startDate()).endDate(auctionRequest.endDate()).status(auctionRequest.status()).build();

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

    public Auction returnOneAuction(Long auctionId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException {
        String email = tokenAttributesExtractor.extractEmailFromToken(authentication);

        Optional<Auction> auction = auctionRepository.findById(auctionId);
        if (auction.isPresent() && !Objects.equals(auction.get().getAuctioner().getEmail(), email)) {
            throw new MismatchedEmailException("You don't have access to this resource");
        } else if (auction.isEmpty()) {
            throw new ResourceNotFoundException("Item not found");
        }
        return auction.get();
    }

    public List<AuctionResponseDTO> returnAllAuctions() {
        List<Auction> allAuctions = auctionRepository.findAll();
        return allAuctions.stream().map(auctionMapperService::returnAuctionResponse).toList();
    }

    @Override
    public AuctionResponseDTO updateAuctionWithNoBid(EditAuctionRequestDTO editAuctionRequestDTO, Authentication authentication) throws MismatchedEmailException, ForbiddenActionException, ResourceNotFoundException {
        String email = tokenAttributesExtractor.extractEmailFromToken(authentication);

        Auction auctionToEdit = validateAuctionUpdateRequest(editAuctionRequestDTO, email);

        if (editAuctionRequestDTO.reservedPrice() != null) auctionToEdit.setReservedPrice(editAuctionRequestDTO.reservedPrice());
        if (editAuctionRequestDTO.endDate() != null) auctionToEdit.setEndDate(editAuctionRequestDTO.endDate());

        return auctionMapperService.returnAuctionResponse(auctionRepository.save(auctionToEdit));
    }

    private Auction validateAuctionUpdateRequest(EditAuctionRequestDTO editAuctionRequestDTO, String email) throws ResourceNotFoundException, MismatchedEmailException, ForbiddenActionException {
        Optional<Auction> auctionResult = auctionRepository.findById(editAuctionRequestDTO.auctionId());
        if (auctionResult.isPresent() && !Objects.equals(auctionResult.get().getAuctioner().getEmail(), email)) throw new MismatchedEmailException("You don't have access to this resource");
        if (auctionResult.isEmpty()) throw new ResourceNotFoundException("Item not found");
        System.out.println(auctionResult.get().getBids());
        if (!auctionResult.get().getBids().isEmpty()) throw new ForbiddenActionException("Cannot perform this action because item has bids");
        return auctionResult.get();
    }
}
