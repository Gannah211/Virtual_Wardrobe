package com.gannah.VirtualWardrobe.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutfitItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="outfit_id", nullable = false)
    private Outfit outfit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="clothing_item_id", nullable = false)
    private ClothingItem clothingItem;

}
