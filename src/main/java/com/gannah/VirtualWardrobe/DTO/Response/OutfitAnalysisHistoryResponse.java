package com.gannah.VirtualWardrobe.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutfitAnalysisHistoryResponse {
    private Long id;
    private List<ItemSummary> items;
    private OutfitAnalysisResponse result;
    private java.time.LocalDateTime createdAt;

    @Data
    @Builder
    public static class ItemSummary {
        private Long id;
        private String imgUrl;
        private String color;
        private String categoryName;
    }
}
