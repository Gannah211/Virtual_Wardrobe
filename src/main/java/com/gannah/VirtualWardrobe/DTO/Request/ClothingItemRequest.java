package com.gannah.VirtualWardrobe.DTO.Request;

import com.gannah.VirtualWardrobe.Model.Occasion;
import com.gannah.VirtualWardrobe.Model.Season;
import jakarta.validation.constraints.NotBlank;
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
public class ClothingItemRequest {

    @NotBlank(message = "Color is required")
    private String color;

    @NotBlank(message = "img is required")
    private String imgUrl;

    private String note;

    private boolean isComfortable;

    @Size(min = 1, message = "At least one occasion is required")
    private List<Occasion> ocassionList;

    private Season season;

    private Long categoryId;

}
