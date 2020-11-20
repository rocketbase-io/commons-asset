package io.rocketbase.commons.dto.batch;

import lombok.*;
import org.springframework.lang.Nullable;

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
    @Nullable
    private Boolean useCache;

    @Singular
    @NotNull
    private List<AssetBatchWriteEntry> entries;
}
