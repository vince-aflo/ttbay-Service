package io.turntabl.ttbay.controller;

import io.turntabl.ttbay.dto.ItemRequest;
import io.turntabl.ttbay.exceptions.ForbiddenActionException;
import io.turntabl.ttbay.exceptions.ItemAlreadyOnAuctionException;
import io.turntabl.ttbay.exceptions.MismatchedEmailException;
import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/on-auction")
    public ResponseEntity<List<Item>> getAllUserAuctionItems(Authentication authentication) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.returnAllAuctionItemsByUser(authentication));
    }
    @PostMapping("/add")
    public ResponseEntity<String> createItem(@RequestBody ItemRequest itemRequest,Authentication authentication) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.addItem(itemRequest,authentication));
    }
    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> removeItem(@PathVariable Long itemId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException, ItemAlreadyOnAuctionException {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.deleteItem(itemId,authentication));
    }
    @GetMapping("/all-by-user")
    public ResponseEntity<List<Item>> getAllUserItems(Authentication authentication) throws ResourceNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.returnAllItemsByUser(authentication));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getOneItemOfUser(@PathVariable Long itemId, Authentication authentication) throws ResourceNotFoundException, MismatchedEmailException {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.returnOneItemOfUser(itemId, authentication));
    }

    @DeleteMapping("/on-auction/{itemId}")
    public ResponseEntity<String> deleteItem(@PathVariable Long itemId,Authentication authentication) throws ForbiddenActionException, ResourceNotFoundException, MismatchedEmailException {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.deleteItemOnAuction(itemId,authentication));
    }

}
