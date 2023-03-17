package io.turntabl.ttbay.service;

import io.turntabl.ttbay.dto.ItemResponseDTO;
import io.turntabl.ttbay.model.Item;

public interface ItemMapperService {
    ItemResponseDTO returnItemResponse(Item item);
}
