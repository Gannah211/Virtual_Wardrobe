package com.gannah.VirtualWardrobe.Controller;

import com.gannah.VirtualWardrobe.DTO.Request.OutfitAdviceRequest;
import com.gannah.VirtualWardrobe.DTO.Response.OutfitAdviceResponse;
import com.gannah.VirtualWardrobe.Service.AI.OutfitAdviceProvider;
import com.gannah.VirtualWardrobe.Service.AI.OutfitAdviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/outfit-advice")
public class OutfitAdviceController {
    private final OutfitAdviceService outfitAdviceService;

    @PostMapping
    public ResponseEntity<OutfitAdviceResponse> getAdvice(@RequestBody OutfitAdviceRequest request){
        return ResponseEntity.ok(outfitAdviceService.getAdvice(request));
    }
}
