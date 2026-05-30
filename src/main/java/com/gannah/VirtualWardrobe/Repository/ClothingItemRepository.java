package com.gannah.VirtualWardrobe.Repository;

import com.gannah.VirtualWardrobe.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClothingItemRepository extends JpaRepository<ClothingItem, Long> {
    List<ClothingItem> findByUserAndCategory(User user, Category category);
    List<ClothingItem> findByIsComfortableTrue(Boolean comfort);
    List<ClothingItem> findByUserAndOccasionListContaining(User user,Occasion occasion);
    List<ClothingItem> findByIsComfortableFalse(Boolean comfort);
    List<ClothingItem> findByUser(User user);
    List<ClothingItem> findByUserAndSeason(User user, Season season);

}
