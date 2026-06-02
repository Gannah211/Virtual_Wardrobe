package com.gannah.VirtualWardrobe.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClothingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String color;

    @Column(name = "img_url", nullable = false, columnDefinition = "LONGTEXT")
    private String imgUrl;

    @Column(nullable = false)
    private String note;

    @Column(nullable = false)
    private boolean isComfortable= true;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "clothing_item_occasions", joinColumns = @JoinColumn(name = "clothing_item_id"))
    @Column(name = "occasion", length = 50)
    private List<Occasion> occasionList;

    @Enumerated(EnumType.STRING)
    private Season season;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "clothingItem", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<OutfitItem> outfitItems = new ArrayList<>();
}
