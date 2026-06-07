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
public class OutfitAnalysisRequest {
    private String userImageBase64;
    private List<Long> clothingItemIds;
}
