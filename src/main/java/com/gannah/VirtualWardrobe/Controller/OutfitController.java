package com.gannah.VirtualWardrobe.Controller;

import com.gannah.VirtualWardrobe.DTO.Request.OutfitRequest;
import com.gannah.VirtualWardrobe.DTO.Response.OutfitResponse;
import com.gannah.VirtualWardrobe.Service.OutfitService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/outfit")
public class OutfitController {
    @Autowired
    private final OutfitService outfitService;

    @PostMapping
    public ResponseEntity<OutfitResponse> createOutfit(@RequestBody OutfitRequest outfitRequest) {
        return ResponseEntity.ok(outfitService.createOutfit(outfitRequest));
    }

    @DeleteMapping("/{outfitId}")
    public ResponseEntity<Void> deleteOutfit(@PathVariable Long outfitId) {
        outfitService.deleteUserOutfit(outfitId);
        return  ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<OutfitResponse>> getUserAllOutfits() {
        return ResponseEntity.ok(outfitService.getUserOutfits());
    }
    @PostMapping("/update-Outfit/{outfitId}")
    public ResponseEntity<OutfitResponse> updateOutfit(@PathVariable Long outfitId,@RequestBody OutfitRequest request) {
        return ResponseEntity.ok(outfitService.updateOutfit(outfitId,request));
    }
}
