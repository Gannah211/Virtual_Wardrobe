package com.gannah.VirtualWardrobe.Service;

import com.gannah.VirtualWardrobe.DTO.Request.OutfitRequest;
import com.gannah.VirtualWardrobe.DTO.Response.ClothingItemResponse;
import com.gannah.VirtualWardrobe.DTO.Response.OutfitResponse;
import com.gannah.VirtualWardrobe.Model.*;
import com.gannah.VirtualWardrobe.Repository.ClothingItemRepository;
import com.gannah.VirtualWardrobe.Repository.OutfitItemRepository;
import com.gannah.VirtualWardrobe.Repository.OutfitRepository;
import com.gannah.VirtualWardrobe.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OutfitService {
    private final ClothingItemRepository clothingItemRepository;
    private final OutfitRepository outfitRepository;
    private final OutfitItemRepository outfitItemRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        String email = "gannah@gmail.com";
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
    public OutfitResponse createOutfit(OutfitRequest Request) {
        User user =getAuthenticatedUser();

        String requestSignature = Request.getClothingItemsIds().stream().sorted().map(String::valueOf).collect(Collectors.joining(","));

        boolean OutfitExists = outfitRepository.existsByoutfitSignature(requestSignature);
        if(OutfitExists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Outfit already exists");
        }
        Outfit outfit = Outfit.builder()
                .name(Request.getName())
                .description(Request.getDescription())
                .outfitSignature(requestSignature)
                .user(user)
                .outfitItems(new ArrayList<>())
                .build();
        Outfit savedOutfit = outfitRepository.save(outfit);

        List<OutfitItem> outfitItems = Request.getClothingItemsIds().stream()
                .map(itemId ->{
                    ClothingItem clothingItem = clothingItemRepository.findById(itemId)
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, "Clothing item not found with id: " + itemId
                            ));
                    return OutfitItem.builder()
                            .outfit(savedOutfit)
                            .clothingItem(clothingItem)
                            .build();
                }).collect(Collectors.toList());

        savedOutfit.setOutfitItems(outfitItems);
        return mapToResponse(outfitRepository.save(savedOutfit));
    }

    @Transactional
    public OutfitResponse updateOutfit(Long outfitId,OutfitRequest Request) {
        Outfit outfit = outfitRepository.findById(outfitId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Outfit not found with id: " + outfitId));
        outfit.setName(Request.getName());
        outfit.setDescription(Request.getDescription());
        outfit.getOutfitItems().clear();
        System.out.println("outfit items before adding the new :" +outfit.getOutfitItems().size());
        Outfit savedOutfit = outfitRepository.save(outfit);

        List<OutfitItem> newOutfitItems = Request.getClothingItemsIds().stream()
                .map(itemId -> {
                    ClothingItem clothingItem = clothingItemRepository.findById(itemId)
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, "Clothing item not found with id: " + itemId
                            ));
                    return OutfitItem.builder()
                            .outfit(outfit)
                            .clothingItem(clothingItem)
                            .build();
                }).toList();
        savedOutfit.getOutfitItems().addAll(newOutfitItems);
        return mapToResponse(outfitRepository.save(savedOutfit));
    }

    @Transactional
    public void deleteUserOutfit(Long outfitId) {
        Outfit outfit = outfitRepository.findById(outfitId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Outfit not found: " + outfitId));
        outfitItemRepository.deleteByOutfitId(outfitId);
        outfitRepository.deleteOutfit(outfitId);
    }

    public List<OutfitResponse> getUserOutfits() {
        User user = getAuthenticatedUser();
        return outfitRepository.findByUser(user).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private OutfitResponse mapToResponse(Outfit outfit) {
        List<ClothingItemResponse> items = outfit.getOutfitItems().stream().map(
                outfitItem -> {
                    ClothingItem ci = outfitItem.getClothingItem();
                    return ClothingItemResponse.builder()
                            .id(ci.getId())
                            .color(ci.getColor())
                            .imgUrl(ci.getImgUrl())
                            .note(ci.getNote())
                            .isComfortable(ci.isComfortable())
                            .ocassionList(ci.getOccasionList().stream().map(Occasion::name).collect(Collectors.toList()))
                            .season(ci.getSeason() != null ? ci.getSeason().toString(): null)
                            .categoryName(ci.getCategory().getName())
                            .build();
                }).collect(Collectors.toList());
        return OutfitResponse.builder()
                .id(outfit.getId())
                .name(outfit.getName())
                .description(outfit.getDescription())
                .userId(outfit.getUser().getId())
                .createdAt(outfit.getCreatedAt())
                .items(items)
                .build();
    }
}
