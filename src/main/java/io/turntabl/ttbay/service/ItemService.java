package io.turntabl.ttbay.service;

import io.turntabl.ttbay.exceptions.ResourceNotFoundException;
import io.turntabl.ttbay.model.Item;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ItemService {
List<Item> returnAllAuctionItemsByUser(Authentication authentication) throws ResourceNotFoundException;
}
