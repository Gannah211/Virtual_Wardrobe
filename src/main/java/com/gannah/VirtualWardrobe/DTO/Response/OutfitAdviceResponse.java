package com.gannah.VirtualWardrobe.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutfitAdviceResponse {
    private String advice;
    private String provider;
}
