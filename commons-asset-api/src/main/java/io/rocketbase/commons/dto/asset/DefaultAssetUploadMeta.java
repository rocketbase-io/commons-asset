package io.rocketbase.commons.dto.asset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import javax.annotation.Nullable;

import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonDeserialize
public class DefaultAssetUploadMeta implements AssetUploadMeta {

    @Nullable
    private String systemRefId;

    @Nullable
    private String context;

    @Nullable
    private Map<String, String> keyValues;

    @Nullable
    private Instant eol;
}
