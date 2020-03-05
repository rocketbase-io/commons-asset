package io.rocketbase.commons.dto.asset;

import lombok.*;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAsset implements Serializable {

    @Nullable
    private Instant before;
    @Nullable
    private Instant after;
    @Nullable
    private String originalFilename;
    @Nullable
    private String referenceUrl;
    @Nullable
    private String context;
    @Singular
    @Nullable
    private List<AssetType> types;
}
