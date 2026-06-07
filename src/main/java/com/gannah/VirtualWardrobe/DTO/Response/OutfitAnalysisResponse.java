package com.gannah.VirtualWardrobe.DTO.Response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutfitAnalysisResponse {
    private String verdict;           // "GREAT FIT" / "MIGHT WORK" / "NOT RECOMMENDED"
    private String overallOpinion;    // main summary paragraph
    private String bodyAnalysis;      // body shape/type detected
    private String colorAnalysis;     // undertone vs clothing colors
    private String fitAnalysis;       // how each piece fits the body type
    private String styleAnalysis;     // overall style coherence
    private List<String> pros;        // what works
    private List<String> cons;        // what doesn't
    private List<String> suggestions; // improvement tips
    private int matchScore;           // 0-100
}
