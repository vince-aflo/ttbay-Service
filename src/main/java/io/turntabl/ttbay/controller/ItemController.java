package io.turntabl.ttbay.controller;

import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.service.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/auction/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/on-auction")
    public ResponseEntity<List<Item>> returnAllUserAuctionItems(Authentication authentication) throws ResourceNotFoundException {
        return ResponseEntity.ok(itemService.returnAllAuctionItemsByUser(authentication));
    }
}
