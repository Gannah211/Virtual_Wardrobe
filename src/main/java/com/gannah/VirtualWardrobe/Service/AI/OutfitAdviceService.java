package com.gannah.VirtualWardrobe.Service.AI;

import com.gannah.VirtualWardrobe.DTO.Request.OutfitAdviceRequest;
import com.gannah.VirtualWardrobe.DTO.Response.OutfitAdviceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutfitAdviceService {
    private final OutfitAdviceProvider outfitAdviceProvider;

    public OutfitAdviceResponse getAdvice(OutfitAdviceRequest request){
        return outfitAdviceProvider.getAdvice(request);
    }
}
