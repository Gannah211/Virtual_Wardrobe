package com.gannah.VirtualWardrobe.Controller;


import com.gannah.VirtualWardrobe.DTO.Request.OutfitAnalysisRequest;
import com.gannah.VirtualWardrobe.DTO.Response.OutfitAnalysisHistoryResponse;
import com.gannah.VirtualWardrobe.DTO.Response.OutfitAnalysisResponse;
import com.gannah.VirtualWardrobe.Service.AI.OutfitAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/history")
    public ResponseEntity<List<OutfitAnalysisHistoryResponse>> getAnalysisHistory(){
        return ResponseEntity.ok(outfitAnalysisService.getHistory());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOutfitAnalysis(@PathVariable Long id){
        outfitAnalysisService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
