package io.rocketbase.commons.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Streams;
import io.rocketbase.commons.dto.asset.AssetPreviews;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.dto.asset.ResponsiveImage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AssetPreviewsDeserializer extends JsonDeserializer<AssetPreviews> {

    @Override
    public AssetPreviews deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        Map<PreviewSize, String> previewMap = new HashMap<>();
        ResponsiveImage responsiveImage = null;

        JsonNode node = jsonParser.getCodec()
                .readTree(jsonParser);

        Streams.stream(node.fieldNames())
                .filter(fieldName -> PreviewSize.getByName(fieldName, null) != null)
                .forEach(fieldName -> previewMap.put(PreviewSize.valueOf(fieldName.toUpperCase()), node.get(fieldName)
                        .asText()));

        JsonNode responsiveNode = node.get("responsive");
        if (responsiveNode != null) {
            responsiveImage = new ResponsiveImage();
            if (responsiveNode.get("sizes") != null) {
                responsiveImage.setSizes(responsiveNode.get("sizes").asText());
            }
            if (responsiveNode.get("srcset") != null) {
                responsiveImage.setSrcset(responsiveNode.get("srcset").asText());
            }
            if (responsiveNode.get("src") != null) {
                responsiveImage.setSrc(responsiveNode.get("src").asText());
            }
        }

        return AssetPreviews.builder()
                .previewMap(previewMap)
                .responsive(responsiveImage)
                .build();
    }
}
