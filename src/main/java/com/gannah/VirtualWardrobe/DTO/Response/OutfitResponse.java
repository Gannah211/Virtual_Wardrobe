package com.gannah.VirtualWardrobe.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutfitResponse {
    private Long id;
    private String name;
    private String description;
    private Long userId;
    private List<ClothingItemResponse> items;
    private LocalDateTime createdAt;
}

