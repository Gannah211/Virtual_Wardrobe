package com.gannah.VirtualWardrobe.Service;

import com.gannah.VirtualWardrobe.DTO.Request.ClothingItemRequest;
import com.gannah.VirtualWardrobe.DTO.Response.ClothingItemResponse;
import com.gannah.VirtualWardrobe.Exception.ResourceNotFoundException;
import com.gannah.VirtualWardrobe.Model.*;
import com.gannah.VirtualWardrobe.Repository.CategoryRepository;
import com.gannah.VirtualWardrobe.Repository.ClothingItemOccasionsRepository;
import com.gannah.VirtualWardrobe.Repository.ClothingItemRepository;
import com.gannah.VirtualWardrobe.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClothingItemService {
    private final UserRepository userRepository;
    private final ClothingItemRepository clothingItemRepository;
    private final ClothingItemOccasionsRepository clothingItemOccasionsRepository;
    private final CategoryRepository categoryRepository;

    public List<ClothingItemResponse> getUserAllClothingItems() {
        User user = getAuthenticatedUser();
        return clothingItemRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private User getAuthenticatedUser() {
       String email = SecurityContextHolder.getContext().getAuthentication().getName();

       return userRepository.findByEmail(email)
               .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public List<ClothingItemResponse> getClothingItemsByCategory(Long categoryId) {
        User user = getAuthenticatedUser();
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        return clothingItemRepository.findByUserAndCategory(user,category).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ClothingItemResponse> getItemsByOccasion(String occasion) {
        User user = getAuthenticatedUser();
        Occasion occasionEnum;
        try{
            occasionEnum = Occasion.valueOf(occasion.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid occasion" + occasion);
        }
        return clothingItemRepository.findByUserAndOccasionListContaining(user,occasionEnum).stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    public List<ClothingItemResponse> getItemsBySeason(Season season) {
        User user = getAuthenticatedUser();
        return clothingItemRepository.findByUserAndSeason(user,season).stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    public List<ClothingItemResponse> getItemsByComfort(boolean comfort) {
        User user = getAuthenticatedUser();
        if (comfort) {
            return clothingItemRepository.findByUserAndIsComfortableTrue(user,comfort).stream().map(this::mapToResponse).collect(Collectors.toList());
        }
        return clothingItemRepository.findByUserAndIsComfortableFalse(user,comfort).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public ClothingItemResponse addClothingItem(ClothingItemRequest request) {
        User user = getAuthenticatedUser();
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow( () -> new ResourceNotFoundException("Category not found"));
        boolean itemExist = clothingItemRepository.existsByUserAndImgUrl(user,request.getImgUrl());
        if(itemExist){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item already exists,try something else dear");
        }
        ClothingItem clothingItem = ClothingItem.builder()
                .color(request.getColor())
                .imgUrl(request.getImgUrl())
                .isComfortable(request.isComfortable())
                .note(request.getNote())
                .occasionList(request.getOcassionList())
                .season(request.getSeason())
                .category(category)
                .user(user)
                .build();
        return mapToResponse(clothingItemRepository.save(clothingItem));
    }
    public ClothingItemResponse updateClothingItem(Long itemId,ClothingItemRequest request) {
        User user = getAuthenticatedUser();
        ClothingItem clothingItem = clothingItemRepository.findById(itemId).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow( () -> new ResourceNotFoundException("Category not found"));
        clothingItem.setColor(request.getColor());
        clothingItem.setImgUrl(request.getImgUrl());
        clothingItem.setNote(request.getNote());
        clothingItem.setSeason(request.getSeason());
        clothingItem.setCategory(category);
        clothingItem.setComfortable(request.isComfortable());
        clothingItem.setOccasionList(request.getOcassionList());
        clothingItemRepository.save(clothingItem);
        return mapToResponse(clothingItem);

    }

    @Transactional
    public void deleteClothingItem(Long id) {
        ClothingItem clothingItem = clothingItemRepository.findById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clothing Item not found"));
        clothingItemOccasionsRepository.deleteItemOccasions(clothingItem.getId());
        clothingItemRepository.deleteItem(clothingItem.getId());
    }


    private ClothingItemResponse mapToResponse(ClothingItem clothingItem) {
        return ClothingItemResponse.builder()
                .id(clothingItem.getId())
                .color(clothingItem.getColor())
                .imgUrl(clothingItem.getImgUrl())
                .note(clothingItem.getNote())
                .isComfortable(clothingItem.isComfortable())
                .categoryName(clothingItem.getCategory().getName())
                .season(clothingItem.getSeason()!= null ? clothingItem.getSeason().toString() : null)
                .categoryId(clothingItem.getCategory() != null ? clothingItem.getCategory().getId() : null)
                .ocassionList(clothingItem.getOccasionList().stream().map(Occasion::toString).collect(Collectors.toList()))
                .build();
    }
}
