package io.rocketbase.commons.dto.batch;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetBatchWrite implements Serializable {

    /**
     * when enabled pre check if downloadUrl has already been downloaded - then take it
     */
    private Boolean useCache;

    @Singular
    @NotNull
    private List<AssetBatchWriteEntry> entries;
}
