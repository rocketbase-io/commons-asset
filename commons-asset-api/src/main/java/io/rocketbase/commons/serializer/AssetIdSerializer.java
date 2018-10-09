package io.rocketbase.commons.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.rocketbase.commons.dto.asset.AssetId;
import io.rocketbase.commons.dto.asset.AssetPreviews;
import io.rocketbase.commons.dto.asset.PreviewSize;

import java.io.IOException;
import java.util.Map;

public class AssetIdSerializer extends JsonSerializer<AssetId> {

    @Override
    public void serialize(AssetId value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        jsonGenerator.writeString(value.getValue());
    }
}
