package io.turntabl.ttbay.service;

import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.Item;
import org.springframework.security.core.Authentication;


import java.util.List;


public interface ItemService {
List<Item> returnAllAuctionItemsByUser(Authentication authentication) throws ResourceNotFoundException;
}
