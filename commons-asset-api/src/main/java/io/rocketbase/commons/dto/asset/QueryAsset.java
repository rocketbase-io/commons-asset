package io.rocketbase.commons.dto.asset;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryAsset {

    private LocalDateTime before;
    private LocalDateTime after;
    private String originalFilename;
    private String referenceUrl;
    @Singular
    private List<AssetType> types;
}
