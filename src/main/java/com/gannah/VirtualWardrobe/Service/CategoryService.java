package com.gannah.VirtualWardrobe.Service;

import com.gannah.VirtualWardrobe.DTO.Response.CategoryResponse;
import com.gannah.VirtualWardrobe.Model.Category;
import com.gannah.VirtualWardrobe.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private CategoryResponse mapToResponse(Category category) {
          return CategoryResponse.builder()
                  .id(category.getId())
                  .name(category.getName())
                  .description(category.getDescription())
                  .build();
    }
}
