package com.gannah.VirtualWardrobe.Service.AI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gannah.VirtualWardrobe.DTO.Request.OutfitAnalysisRequest;
import com.gannah.VirtualWardrobe.DTO.Response.OutfitAnalysisResponse;
import com.gannah.VirtualWardrobe.Model.ClothingItem;
import com.gannah.VirtualWardrobe.Model.Occasion;
import com.gannah.VirtualWardrobe.Repository.ClothingItemRepository;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutfitAnalysisService {
    private final ClothingItemRepository clothingItemRepository;
    private final GeminiHelpers geminiHelpers;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.model}")
    private String visionModel;

    private static final String SYSTEM_PROMPT = """
            You are Cher from Clueless — bubbly, witty, and completely fashion-obsessed.you also an expert fashion stylist and color analyst with deep knowledge of:
                - Body shape analysis (hourglass, pear, apple, rectangle, inverted triangle)
                - Skin undertone analysis (warm, cool, neutral) from visual cues
                - Color theory and which colors flatter different undertones
                - How clothing cuts and silhouettes complement different body types
                - Style coherence and outfit coordination
    
                You will receive a full-body photo of a person and photos of clothing items they\s
                want to wear. Analyze everything carefully and give honest, constructive fashion advice but also be fun, dramatic, and specific and never say insults.
    
                Always respond in this EXACT JSON format with no extra text, keep all strings under 80 chars:
                {
                 "verdict": "GREAT FIT" | "MIGHT WORK" | "NOT RECOMMENDED",
                 "matchScore": <0-100>,
                 "overallOpinion": "<max 80 chars>",
                 "bodyAnalysis": "<max 60 chars>",
                 "colorAnalysis": "<max 60 chars>",
                 "fitAnalysis": "<max 60 chars>",
                 "styleAnalysis": "<max 60 chars>",
                 "pros": ["<max 3 items, 40 chars each>"],
                 "cons": ["<max 2 items, 40 chars each>"],
                 "suggestions": ["<max 2 items, 40 chars each>"]
                }
            """;

    public OutfitAnalysisResponse analyze(OutfitAnalysisRequest request) {
        if(request.getUserImageBase64() == null || request.getUserImageBase64().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing user image");
        }
        if(request.getClothingItemIds() == null || request.getClothingItemIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing Clothing Item IDs");
        }

        List<ClothingItem> items = clothingItemRepository.findAllById(request.getClothingItemIds());

        if(items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Clothing items not found");
        }
        return callGeminiForAnalysis(request.getUserImageBase64(), items);
    }

    private OutfitAnalysisResponse callGeminiForAnalysis(String userImageBase64, List<ClothingItem> items) {
        try{
            Client client = Client.builder().apiKey(geminiApiKey).build();
            List<Part> parts = new ArrayList<>();

            String resizedUser = geminiHelpers.resizeImageForAI(userImageBase64);
            String mimeType = detectMimeType(resizedUser);
            String cleanB64= stripDataUrlPrefix(resizedUser);
            parts.add(Part.fromBytes(Base64.getDecoder().decode(cleanB64), mimeType));

            for(ClothingItem item : items) {
                if(item.getImgUrl() != null && !item.getImgUrl().isBlank()) {
                    String resized =geminiHelpers.resizeImageForAI(item.getImgUrl());
                    if(geminiHelpers.hasImage(resized)) {
                        String itemMime = detectMimeType(resized);
                        String itemB64 =  stripDataUrlPrefix(resized);
                        parts.add(Part.fromBytes(Base64.getDecoder().decode(itemB64), itemMime));
                    }
                }
            }

            parts.add(Part.fromText(buildAnalysisPrompt(items)));

            Content content = Content.fromParts(parts.toArray(new Part[0]));

            GenerateContentConfig config = GenerateContentConfig.builder()
                    .systemInstruction(Content.fromParts(Part.fromText(SYSTEM_PROMPT)))
                    .maxOutputTokens(2048)
                    .temperature(0.4f)
                    .build();

            GenerateContentResponse response =
                    client.models.generateContent(visionModel, content, config);

            String rawJson = response.text();
            log.info("Gemini analysis response: {}", rawJson);

            return parseAnalysisResponse(rawJson);
        }catch (ResponseStatusException e){
            throw e;
        }catch (Exception e){
            log.error("Gemini outfit analysis failed: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Analysis failed: " + e.getMessage());
        }
    }

    private String buildAnalysisPrompt(List<ClothingItem> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("Please analyze if this outfit will suit this person.\n\n");
        sb.append("The outfit consists of:\n");

        for (ClothingItem item : items) {
            String occasions = (item.getOccasionList() != null && !item.getOccasionList().isEmpty())
                    ? item.getOccasionList().stream()
                    .map(Occasion::name)
                    .collect(Collectors.joining(", "))
                    : "any occasion";
            sb.append(String.format("- %s: %s color, %s season, %s style\n",
                    item.getCategory() != null ? item.getCategory().getName() : "item",
                    item.getColor()    != null ? item.getColor()              : "unknown",
                    item.getSeason()   != null ? item.getSeason().name()      : "any",
                    occasions
            ));
        }

        sb.append("\nAnalyze:\n");
        sb.append("1. The person's body shape and proportions\n");
        sb.append("2. Their skin undertone (warm/cool/neutral) from the photo\n");
        sb.append("3. Whether these clothing colors flatter their undertone\n");
        sb.append("4. Whether these clothing cuts/silhouettes suit their body type\n");
        sb.append("5. Whether the outfit pieces work together as a cohesive look\n");
        sb.append("6. Whether the occasions are appropriate and consistent\n");
        sb.append("7. Overall verdict with specific, actionable suggestions\n");
        sb.append("\nBe honest but constructive. Give specific reasons for each point.");

        return sb.toString();
    }

    private OutfitAnalysisResponse parseAnalysisResponse(String rawJson) {
        try {
            // Strip markdown code blocks
            String clean = rawJson
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();

            // If JSON is truncated, attempt to close it so Jackson can still parse
            // what was received rather than failing completely
            clean = repairTruncatedJson(clean);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(clean);

            List<String> pros        = new ArrayList<>();
            List<String> cons        = new ArrayList<>();
            List<String> suggestions = new ArrayList<>();

            if (root.has("pros"))        root.get("pros")       .forEach(n -> pros.add(n.asText()));
            if (root.has("cons"))        root.get("cons")       .forEach(n -> cons.add(n.asText()));
            if (root.has("suggestions")) root.get("suggestions").forEach(n -> suggestions.add(n.asText()));

            return OutfitAnalysisResponse.builder()
                    .verdict(root.path("verdict").asText("MIGHT WORK"))
                    .matchScore(root.path("matchScore").asInt(50))
                    .overallOpinion(root.path("overallOpinion").asText("See full analysis below."))
                    .bodyAnalysis(root.path("bodyAnalysis").asText(""))
                    .colorAnalysis(root.path("colorAnalysis").asText(""))
                    .fitAnalysis(root.path("fitAnalysis").asText(""))
                    .styleAnalysis(root.path("styleAnalysis").asText(""))
                    .pros(pros)
                    .cons(cons)
                    .suggestions(suggestions)
                    .build();

        } catch (Exception e) {
            log.error("Failed to parse Gemini response: {}", rawJson);
            return OutfitAnalysisResponse.builder()
                    .verdict("MIGHT WORK")
                    .matchScore(50)
                    .overallOpinion("Analysis completed. Please try again for full details.")
                    .pros(List.of())
                    .cons(List.of())
                    .suggestions(List.of())
                    .build();
        }
    }

    /**
     * Attempts to close a truncated JSON object so Jackson can parse
     * whatever fields were received before the cutoff.
     */
    private String repairTruncatedJson(String json) {
        if (json == null || json.isBlank()) return "{}";

        // Already valid — don't touch it
        if (json.endsWith("}")) return json;

        StringBuilder sb = new StringBuilder(json);

        // If we're mid-string value, close the string
        // Count unescaped quotes to detect open string
        long quoteCount = json.chars()
                .filter(c -> c == '"')
                .count();
        boolean insideString = (quoteCount % 2) != 0;
        if (insideString) sb.append("\"");

        // Close any open arrays
        long openBrackets  = json.chars().filter(c -> c == '[').count();
        long closeBrackets = json.chars().filter(c -> c == ']').count();
        for (long i = 0; i < openBrackets - closeBrackets; i++) sb.append("]");

        // Close the object
        sb.append("}");

        return sb.toString();
    }

    private String stripDataUrlPrefix(String dataUrl) {
        if (dataUrl == null) return "";
        int idx = dataUrl.indexOf(',');
        return idx >= 0 ? dataUrl.substring(idx + 1) : dataUrl;
    }

    private String detectMimeType(String dataUrl) {
        if (dataUrl == null || !dataUrl.startsWith("data:")) return "image/jpeg";
        try { return dataUrl.split(";")[0].replace("data:", ""); }
        catch (Exception e) { return "image/jpeg"; }
    }
}
