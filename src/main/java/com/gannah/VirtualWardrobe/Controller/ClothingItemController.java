package com.gannah.VirtualWardrobe.Controller;

import com.gannah.VirtualWardrobe.DTO.Request.ClothingItemRequest;
import com.gannah.VirtualWardrobe.DTO.Response.ClothingItemResponse;
import com.gannah.VirtualWardrobe.Service.ClothingItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/items")
public class ClothingItemController {
    private final ClothingItemService clothingItemService;

    @GetMapping
    public ResponseEntity<List<ClothingItemResponse>> getClothingItems() {
        return ResponseEntity.ok(clothingItemService.getUserAllClothingItems());
    }

    @PostMapping
    public ResponseEntity<ClothingItemResponse> addClothingItem(@Valid @RequestBody ClothingItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clothingItemService.addClothingItem(request));
    }
    @GetMapping("/occasions/{occasion}")
    public ResponseEntity<List<ClothingItemResponse>>  getClothingItemsByOccasion(@PathVariable String occasion) {
        return ResponseEntity.ok((clothingItemService.getItemsByOccasion(occasion)));
    }

    @GetMapping("/category/{categoryID}")
    public ResponseEntity<List<ClothingItemResponse>> getClothingItemsByCategory(@PathVariable Long categoryID) {
        return ResponseEntity.ok(clothingItemService.getClothingItemsByCategory(categoryID));
    }
}
