package io.rocketbase.commons.dto.asset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.rocketbase.commons.serializer.AssetIdDeserializer;
import io.rocketbase.commons.serializer.AssetIdSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;


/**
 * used to trick with mapstruct in order to autoconvert id -> {@link DefaultAssetReference} or {@link AssetRead}
 */
@Data
@RequiredArgsConstructor
@JsonSerialize(using = AssetIdSerializer.class)
@JsonDeserialize(using = AssetIdDeserializer.class)
@Schema(description = "used to trick with mapstruct in order to autoconvert id -> DefaultAssetReference or AssetRead")
public class AssetId {

    private final String value;

}
