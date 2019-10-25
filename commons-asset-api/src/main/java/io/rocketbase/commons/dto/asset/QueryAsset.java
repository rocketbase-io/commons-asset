package io.rocketbase.commons.dto.asset;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAsset implements Serializable {

    private Instant before;
    private Instant after;
    private String originalFilename;
    private String referenceUrl;
    private String context;
    @Singular
    private List<AssetType> types;
}
