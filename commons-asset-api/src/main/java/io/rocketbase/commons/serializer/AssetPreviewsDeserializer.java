package io.rocketbase.commons.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.rocketbase.commons.dto.asset.AssetPreviews;
import io.rocketbase.commons.dto.asset.PreviewSize;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AssetPreviewsDeserializer extends JsonDeserializer<AssetPreviews> {

    @Override
    public AssetPreviews deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        Map<PreviewSize, String> previewMap = new HashMap<>();

        JsonNode node = jsonParser.getCodec()
                .readTree(jsonParser);
        node.fieldNames()
                .forEachRemaining(fieldname -> previewMap.put(PreviewSize.valueOf(fieldname.toUpperCase()), node.get(fieldname)
                        .asText()));
        return AssetPreviews.builder()
                .previewMap(previewMap)
                .build();
    }
}
