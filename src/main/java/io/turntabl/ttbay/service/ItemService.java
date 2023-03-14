package io.turntabl.ttbay.service;

import io.turntabl.ttbay.dto.ItemRequest;
import io.turntabl.ttbay.exceptions.ForbiddenActionException;
import io.turntabl.ttbay.exceptions.ItemAlreadyOnAuctionException;
import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.Item;
import org.springframework.security.core.Authentication;

import java.util.List;


public interface ItemService {
    List<Item> returnAllAuctionItemsByUser(Authentication authentication) throws ResourceNotFoundException;

    List<Item> returnAllItemsByUser(Authentication authentication) throws ResourceNotFoundException;

    String addItem(ItemRequest itemRequest, Authentication authentication) throws ResourceNotFoundException;

    Item returnOneItemOfUser(Long itemId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException;

    String deleteDraftItem(Long itemId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException, ItemAlreadyOnAuctionException;

    String updateItem(Long itemId, ItemRequest itemRequest, Authentication authentication) throws MismatchedEmailException, ResourceNotFoundException;

    String deleteItemOnAuction(Long itemId, Authentication authentication) throws ResourceNotFoundException, ForbiddenActionException, MismatchedEmailException;

}
