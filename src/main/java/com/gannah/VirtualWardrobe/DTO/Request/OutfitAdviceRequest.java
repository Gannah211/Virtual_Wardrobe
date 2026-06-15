package com.gannah.VirtualWardrobe.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutfitAdviceRequest {
    private List<String> itemsImgUrls;
    private List<String> itemsCategory;
    private List<String> itemsColors;
    private String prompt;
}
