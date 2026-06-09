package com.gannah.VirtualWardrobe.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutfitAnalysisHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "cache_key",nullable = false,length = 100)
    private String cacheKey;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "outfit_analysis_items",
            joinColumns = @JoinColumn(name="analysis_id"),
            inverseJoinColumns = @JoinColumn(name = "clothing_item_id")
    )
    private List<ClothingItem> clothingItems = new ArrayList<>();

    @Column(name = "response_json",columnDefinition = "LONGTEXT", nullable = false)
    private String responseJson;

    @Column(name = "created_at",nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

}
