package com.gannah.VirtualWardrobe.Controller;


import com.gannah.VirtualWardrobe.DTO.Request.OutfitAnalysisRequest;
import com.gannah.VirtualWardrobe.DTO.Response.OutfitAnalysisResponse;
import com.gannah.VirtualWardrobe.Service.AI.OutfitAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/outfit-Analysis")
public class OutfitAnalysisController {
    @Autowired
    private final OutfitAnalysisService outfitAnalysisService;

    @PostMapping
    public ResponseEntity<OutfitAnalysisResponse> willItSuitMe(
            @RequestBody OutfitAnalysisRequest request) {
        return ResponseEntity.ok(outfitAnalysisService.analyze(request));
    }
}
