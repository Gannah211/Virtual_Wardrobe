package com.gannah.VirtualWardrobe.DTO.Request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutfitRequest {
    @NotBlank(message = "your outfit should have a name ")
    private String name;
    private String description;
    @NotNull(message = "your outfit should have items")
    @Size(min=1, message = "An outfit must have at least one item")
    private List<Long> clothingItemsIds;
}
