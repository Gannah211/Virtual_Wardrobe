package com.gannah.VirtualWardrobe.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutfitAdviceRequest {
    private String topImgUrl;
    private String bottomImgUrl;
    private String topColor;
    private String bottomColor;
    private String topCategory;
    private String bottomCategory;
    private String prompt;
}
