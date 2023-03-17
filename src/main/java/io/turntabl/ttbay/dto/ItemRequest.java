package io.turntabl.ttbay.dto;

import io.turntabl.ttbay.enums.Category;
import io.turntabl.ttbay.enums.ItemCondition;
import io.turntabl.ttbay.model.ItemImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;


public record ItemRequest(@NotBlank(message = "name has to be set") String name,
                          @NotBlank String description,
                          ItemCondition condition,
                          @NotNull(message = "Category has to be set") Category category,
                          @NotNull(message = "Image has to be set") List<ItemImage> imageList)
                           {
}
