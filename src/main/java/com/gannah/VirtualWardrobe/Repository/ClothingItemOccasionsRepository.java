package com.gannah.VirtualWardrobe.Repository;

import com.gannah.VirtualWardrobe.Model.ClothingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClothingItemOccasionsRepository extends JpaRepository<ClothingItem,Long> {
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM clothing_item_occasions WHERE clothing_item_id = :itemId")
    void deleteItemOccasions(@Param("itemId") Long itemId);
}
