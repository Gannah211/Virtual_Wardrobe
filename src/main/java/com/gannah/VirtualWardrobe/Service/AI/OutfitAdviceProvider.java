package com.gannah.VirtualWardrobe.Service.AI;

import com.gannah.VirtualWardrobe.DTO.Request.OutfitAdviceRequest;
import com.gannah.VirtualWardrobe.DTO.Response.OutfitAdviceResponse;
import org.springframework.data.repository.query.Param;

public interface OutfitAdviceProvider {
    OutfitAdviceResponse getAdvice(OutfitAdviceRequest request);
    String getProviderName();
}
