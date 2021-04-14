package io.rocketbase.commons.dto.batch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
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

    @NotNull
    private List<AssetBatchWriteEntry> entries;

    public AssetBatchWrite withEntry(AssetBatchWriteEntry entry) {
        if (entries == null) {
            entries = new ArrayList<>();
        }
        entries.add(entry);
        return this;
    }

}
