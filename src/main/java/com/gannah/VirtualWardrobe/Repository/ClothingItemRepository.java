package com.gannah.VirtualWardrobe.Repository;

import com.gannah.VirtualWardrobe.Model.Category;
import com.gannah.VirtualWardrobe.Model.ClothingItem;
import com.gannah.VirtualWardrobe.Model.Occasion;
import com.gannah.VirtualWardrobe.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClothingItemRepository extends JpaRepository<ClothingItem, Long> {
    List<ClothingItem> findByUserAndCategory(User user, Category category);
    List<ClothingItem> findByIsComfortableTrue();
    List<ClothingItem> findByOccasionListContaining(Occasion occasion);
    List<ClothingItem> findByIsComfortableFalse();
    List<ClothingItem> findByUser(User user);
}
