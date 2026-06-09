package com.gannah.VirtualWardrobe.Repository;

import com.gannah.VirtualWardrobe.Model.OutfitAnalysisHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OutfitAnalysisHistoryRepository extends JpaRepository<OutfitAnalysisHistory, Long> {

    Optional<OutfitAnalysisHistory> findByUserIdAndCacheKey(Long userId, String cacheKey);
    List<OutfitAnalysisHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
    void deleteByUserId(Long userId);
}
