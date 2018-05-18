package io.rocketbase.commons.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.rocketbase.commons.dto.asset.AssetPreviews;
import io.rocketbase.commons.dto.asset.PreviewSize;

import java.io.IOException;
import java.util.Map;

public class AssetPreviewsSerializer extends JsonSerializer<AssetPreviews> {

    @Override
    public void serialize(AssetPreviews value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        jsonGenerator.writeStartObject();
        for (Map.Entry<PreviewSize, String> entry : value.getPreviewMap()
                .entrySet()) {
            jsonGenerator.writeStringField(entry.getKey()
                    .name()
                    .toLowerCase(), entry.getValue());
        }
        jsonGenerator.writeEndObject();
    }
}
