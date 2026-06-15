package com.gannah.VirtualWardrobe.Service.AI;

import com.gannah.VirtualWardrobe.DTO.Request.OutfitAdviceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.awt.*;

@Slf4j
@Component
public class GeminiHelpers {
    private static final int MAX_IMAGE_SIZE = 512;
    private static final float IMAGE_QUALITY = 0.6f;

    public boolean hasImage(String url){
        return url !=null && !url.isBlank();
    }
    public Map<String, Object> buildImagePart(String imgUrl){

        if(imgUrl.startsWith("data:")){
            String[] parts = imgUrl.split(",",2);
            String mimeType = parts[0].split(";")[0].replace("data:","");
            String base64 = parts[1];
            return Map.of("inline_data", Map.of("mime_type", mimeType,"data", base64));
        }
        return Map.of("file_data", Map.of("file_uri", imgUrl));
    }

    public String buildPromptText(OutfitAdviceRequest request){
        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (String item : request.getItemsImgUrls()){
            if(hasImage(item)){
                sb.append(String.format("Image %d is a %s in %s .",count, request.getItemsCategory().get(count-1), request.getItemsColors().get(count-1)));
            }else{
                sb.append(String.format("item %d is a %s in %s .",count, request.getItemsCategory().get(count-1), request.getItemsColors().get(count-1)));
            }
            count++;
        }
//            if(hasImage(request.getTopImgUrl())){
//            sb.append(String.format("The first image is a %s in %s. ", request.getTopCategory(), request.getTopColor()));
//        }else{
//            sb.append(String.format("Top: a %s in %s(no image).", request.getTopCategory(), request.getTopColor()));
//        }
//
//        if(hasImage(request.getBottomImgUrl())){
//            sb.append(String.format("The second image is a %s in %s.",request.getBottomCategory(), request.getBottomColor()));
//        }else{
//            sb.append(String.format(
//                    "Bottom: a %s in %s (no image). ",
//                    request.getBottomCategory(), request.getBottomColor()
//            ));
//        }
        sb.append("Question: ").append(request.getPrompt());
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public String extractText(Map response){
        try{
            List<Map<String,Object>> candidates = (List<Map<String, Object>>)response.get("candidates");
            Map<String, Object> content = (Map<String,Object>) candidates.get(0).get("content");
            List<Map<String,Object>> parts= (List<Map<String,Object>>)content.get("parts");
            return (String) parts.get(0).get("text");
        }catch (Exception e){
            log.error("Could not parse Gemini response: {}", response);
            return "As if! The fashion masters are speechless. Try again!";
        }
    }

    public String resizeImageForAI(String dataUrl){
        if(dataUrl == null || dataUrl.isBlank() ||!dataUrl.startsWith("data:")){
            return dataUrl;
        }
        try{
            String[] parts = dataUrl.split(",",2);
            byte[] imgBytes = Base64.getDecoder().decode(parts[1]);

            BufferedImage orignal = ImageIO.read(new ByteArrayInputStream(imgBytes));
            if(orignal == null) return dataUrl;

            int orignW = orignal.getWidth();
            int orignH = orignal.getHeight();

            if(orignW<= MAX_IMAGE_SIZE && orignH<= MAX_IMAGE_SIZE) return dataUrl;

            int newW , newH;
            if(orignW> orignH){
                newW = MAX_IMAGE_SIZE;
                newH = (int) Math.round((double) orignH * MAX_IMAGE_SIZE / orignW);
            }else{
                newW = (int) Math.round((double) orignW * MAX_IMAGE_SIZE / orignH);
                newH = MAX_IMAGE_SIZE;
            }

            BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resized.createGraphics();

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, newW, newH);

            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(orignal, 0, 0, newW, newH, null);
            g.dispose();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(resized, "jpeg", out);
            String resizedBase64 = Base64.getEncoder().encodeToString(out.toByteArray());

            log.info("Resized image from {}x{} to {}x{} ({} bytes → {} bytes)",
                    orignW, orignH, newW, newH, imgBytes.length, out.size());

            return "data:image/jpeg;base64," + resizedBase64;

        }catch (Exception e){
            log.warn("Image resize failed, sending orginal: {}", e.getMessage());
            return dataUrl;
        }
    }
}
