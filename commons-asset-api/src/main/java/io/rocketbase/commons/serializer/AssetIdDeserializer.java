package io.rocketbase.commons.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.rocketbase.commons.dto.asset.AssetId;

import java.io.IOException;

public class AssetIdDeserializer extends JsonDeserializer<AssetId> {

    @Override
    public AssetId deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        return new AssetId(jsonParser.getValueAsString());
    }
}
