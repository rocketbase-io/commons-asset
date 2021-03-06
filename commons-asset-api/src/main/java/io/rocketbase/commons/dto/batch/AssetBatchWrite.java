package io.rocketbase.commons.dto.batch;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * batch instruction wrapper
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "batch instruction wrapper")
public class AssetBatchWrite implements Serializable {

    /**
     * when enabled pre check if downloadUrl has already been downloaded - then take it
     */
    @Nullable
    @Schema(description = "when enabled pre check if downloadUrl has already been downloaded - then take it")
    private Boolean useCache;

    /**
     * list of urls with additional information that will be stored in succeeded case.
     */
    @NotNull
    @Schema(description = "list of urls with additional information that will be stored in succeeded case.")
    private List<AssetBatchWriteEntry> entries;

    public AssetBatchWrite withEntry(AssetBatchWriteEntry entry) {
        if (entries == null) {
            entries = new ArrayList<>();
        }
        entries.add(entry);
        return this;
    }

}
