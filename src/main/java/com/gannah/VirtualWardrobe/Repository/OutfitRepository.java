package com.gannah.VirtualWardrobe.Repository;

import com.gannah.VirtualWardrobe.Model.Outfit;
import com.gannah.VirtualWardrobe.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutfitRepository extends JpaRepository<Outfit, Long> {
    List<Outfit> findByUser(User user);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM outfit WHERE id = :outfitId")
    void deleteOutfit(@Param("outfitId") Long outfitId);
}
