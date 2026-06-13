package com.gannah.VirtualWardrobe.Repository;

import com.gannah.VirtualWardrobe.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    boolean existsByUserAndImgUrl(User user, String imgUrl);
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM clothing_item WHERE id = :itemId")
    void deleteItem(@Param("itemId") Long itemId);
}
