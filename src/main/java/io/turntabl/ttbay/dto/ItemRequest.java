package io.turntabl.ttbay.dto;

import io.turntabl.ttbay.enums.Category;
import io.turntabl.ttbay.enums.ItemCondition;
import io.turntabl.ttbay.model.ItemImage;
import io.turntabl.ttbay.model.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;


public record ItemRequest(
        @NotBlank(message = "name has to be set") String name,
        @NotBlank String description,
        ItemCondition condition,
        @NotNull(message = "Category has to be set") Category category,
        List<Tag> tags,
        @NotNull(message = "Image has to be set") List<ItemImage> imageList
){}
