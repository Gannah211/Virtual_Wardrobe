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
public class ClothingItemResponse {
    private Long id;
    private String color;
    private String imgUrl;
    private String note;
    private boolean isComfortable;
    private List<String> ocassionList;
    private String season;
    private String categoryName;
    private Long categoryId;
}
