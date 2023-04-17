package io.turntabl.ttbay.utils.mappers;

import io.turntabl.ttbay.dto.ItemRequest;
import io.turntabl.ttbay.model.Item;
import io.turntabl.ttbay.model.ItemImage;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ItemMapper{
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    default Item itemDTOtoItem(ItemRequest request, Item item){
        Item copyItem = Item.builder().id(item.getId())
                .name(request.name())
                .description(request.description())
                .category(request.category())
                .condition(request.condition())
                .onAuction(item.getOnAuction())
                .isSold(item.getIsSold())
                .user(item.getUser())
                .build();
        copyItem.setImageList(
                request.imageList().
                        stream()
                        .map(itemImage ->
                                new ItemImage(item, itemImage.getImageUrl()))
                        .toList());
        return copyItem;
    }
}
