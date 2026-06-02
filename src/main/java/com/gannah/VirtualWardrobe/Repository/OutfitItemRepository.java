package com.gannah.VirtualWardrobe.Repository;

import com.gannah.VirtualWardrobe.Model.OutfitItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutfitItemRepository extends JpaRepository<OutfitItem, Long> {
    List<OutfitItem> findByOutfitId(Long outfitId);
    @Modifying
    @Query(value = "DELETE FROM Outfit_Item oi WHERE oi.outfit_id = :outfitId",nativeQuery = true)
    void deleteByOutfitId(@Param("outfitId") Long outfitId);
}
