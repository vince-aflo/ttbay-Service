package io.turntabl.ttbay.service.Impl;

import io.turntabl.ttbay.dto.ItemResponseDTO;
import io.turntabl.ttbay.enums.Category;
import io.turntabl.ttbay.enums.ItemCondition;
import io.turntabl.ttbay.enums.OfficeLocation;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.User;
import io.turntabl.ttbay.service.ItemMapperService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ItemMapperServiceImplTest{
    private final User testUser = User.builder().username("aiks").email("test@gmail.com").fullName("Aikins Akenten Dwamena").officeLocation(OfficeLocation.SONNIDOM_HOUSE).build();
    private final Item item = Item.builder()
            .user(testUser)
            .auction(null)
            .id(1L)
            .name("book")
            .isSold(false)
            .imageList(List.of())
            .condition(ItemCondition.NEW)
            .category(Category.BOOKS)
            .onAuction(true)
            .description("test")
            .build();
    private final ItemResponseDTO itemResponseDTO = ItemResponseDTO.builder().auctions(null).description(item.getDescription()).itemId(item.getId()).userEmail(item.getUser().getEmail()).itemName(item.getName()).onAuction(item.getOnAuction()).isSold(item.getIsSold()).condition(item.getCondition()).category(item.getCategory()).imageList(item.getImageList()).auctions(item.getAuction()).build();
    @InjectMocks
    private ItemMapperServiceImpl itemMapperService;

    @Test
    void returnItemResponse_givenItem_shouldReturnItemResponseDto(){
        ItemResponseDTO actualDTO = itemMapperService.returnItemResponse(item);
        Assertions.assertEquals(itemResponseDTO, actualDTO);
    }
}