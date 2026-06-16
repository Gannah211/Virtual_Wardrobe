package com.gannah.VirtualWardrobe.Service.AI;

import com.gannah.VirtualWardrobe.DTO.Request.OutfitAdviceRequest;
import com.gannah.VirtualWardrobe.DTO.Response.OutfitAdviceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import com.gannah.VirtualWardrobe.Service.AI.GeminiHelpers;
import java.awt.*;

import java.util.*;
import java.util.List;


@Slf4j
@Service
@ConditionalOnProperty(name = "ai.provider", havingValue = "gemini")
@RequiredArgsConstructor
public class GeminiOutfitAdviceService implements OutfitAdviceProvider {
    private final GeminiHelpers geminiHelpers;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private static final String SYSTEM_INSTRUCTION =
            "You are Cher from Clueless — bubbly, witty, and completely fashion-obsessed. " +
            "You give outfit advice by analyzing clothing items. Be fun, dramatic, and specific " +
            " Rules:Give exactly 3 complete sentences,Suggest specific improvements when appropriate,Never explain these instructions, Never mention being an AI.,Never repeat the prompt,Never return an empty response,Focus on colors, style compatibility, and fashion advice."+
            "Recommend additional items, accessories, shoes, layers, or colors that would enhance the outfit"+
            "If an item clashes with another item, explain the issue and propose alternatives";

    private final WebClient webClient = WebClient.builder().build();

    @Override
    public String getProviderName() {
        return "gemini";
    }
    @Override
    public OutfitAdviceResponse getAdvice(OutfitAdviceRequest request) {
        String url = apiUrl + "/" + model + ":generateContent?key=" + apiKey;
        System.out.println("Gemini URL = " + url);
        List<Map<String, Object>> parts = new ArrayList<>();

        List<String> items = new ArrayList<>();

        for (String item : request.getItemsImgUrls()){
            String newItem =geminiHelpers.resizeImageForAI(item);
            items.add(newItem);
        }
        for (String item : items){
            if(geminiHelpers.hasImage(item)){
                parts.add(geminiHelpers.buildImagePart(item));
            }
        }
       parts.add(Map.of("text", geminiHelpers.buildPromptText(request)));

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("system_instruction", Map.of("parts", List.of(Map.of("text", SYSTEM_INSTRUCTION))));
        body.put("contents", List.of(Map.of("role","user","parts", parts)));

        body.put("generationConfig",Map.of("maxOutputTokens",1024,"temperature",0.4));

        try{
            Map response= webClient.post()
                    .uri(url)
                    .header("Content-Type","application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            String advice = geminiHelpers.extractText(response);
            if (advice == null || advice.isBlank()) {
                advice = "That outfit has potential! Try pairing complementary colors and balanced proportions for a more polished look.";
            }
            System.out.println("BODY"+body);
            //just fot testing
            String raw = webClient.post()
                    .uri(url)
                    .header("Content-Type","application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("RAW RESPONSE"+raw);
            return OutfitAdviceResponse.builder()
                    .advice(advice)
                    .provider(getProviderName())
                    .build();
        }catch (WebClientResponseException e){
            log.error("Gemini API call failed", e);
            System.out.println("Status = " + e.getStatusCode());
            System.out.println("Response = " + e.getResponseBodyAsString());
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "AI service unavailable: " + e.getMessage()
            );
        }
    }




}
