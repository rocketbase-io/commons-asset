package io.rocketbase.commons.dto.batch;


import io.rocketbase.commons.dto.asset.AssetUploadMeta;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.lang.Nullable;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;


@Data
@AllArgsConstructor
public class AssetBatchWriteEntry implements AssetUploadMeta {

    @NotNull
    private String url;

    @Nullable
    private String systemRefId;

    @Nullable
    private String context;

    @Nullable
    private Map<String, String> keyValues;

    @Nullable
    private Instant eol;

    public AssetBatchWriteEntry() {
    }

    public AssetBatchWriteEntry(String url) {
        this.url = url;
    }

}
