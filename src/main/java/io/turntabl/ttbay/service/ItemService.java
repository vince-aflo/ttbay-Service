package io.turntabl.ttbay.service;

import io.turntabl.ttbay.dto.ItemRequest;
import io.turntabl.ttbay.dto.ItemResponseDTO;
import io.turntabl.ttbay.exceptions.ForbiddenActionException;
import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.Item;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public interface ItemService {
    List<ItemResponseDTO> returnAllAuctionItemsByUser(Authentication authentication) throws ResourceNotFoundException;
    List<ItemResponseDTO> returnAllItemsByUser(Authentication authentication) throws ResourceNotFoundException;
    ItemResponseDTO addItem(ItemRequest itemRequest, Authentication authentication) throws ResourceNotFoundException;
    ItemResponseDTO returnOneItemOfUser(Long itemId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException;
    Item returnOneItem(Authentication authentication, Long itemId) throws ResourceNotFoundException, MismatchedEmailException;
    String deleteDraftItem(Long itemId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException;
    String updateItem(Long itemId, ItemRequest itemRequest, Authentication authentication) throws MismatchedEmailException, ResourceNotFoundException;
    String deleteItemOnAuction(Long itemId, Authentication authentication) throws ResourceNotFoundException, ForbiddenActionException, MismatchedEmailException;
    CompletableFuture<Void> persistExchangedItemsInDb() throws ResourceNotFoundException;
}
