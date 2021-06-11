package io.rocketbase.commons.dto.batch;


import io.rocketbase.commons.dto.asset.AssetUploadMeta;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.lang.Nullable;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;


@Data
@AllArgsConstructor
@Schema(description = "detailed instruction for each url")
public class AssetBatchWriteEntry implements AssetUploadMeta {

    @NotNull
    @Schema(description = "full qualified url to the asset that should been analysed/stored", required = true)
    private String url;

    @Nullable
    @Schema(description = "optional foreign system reference id  that will get stored to the entity")
    private String systemRefId;

    @Nullable
    @Schema(description = "optional context that will get stored to the entity")
    private String context;

    @Nullable
    @Schema(description = "optional keyValues that will get stored to the entity")
    private Map<String, String> keyValues;

    @Nullable
    @Schema(description = "optional eol that will get stored to the entity")
    private Instant eol;

    public AssetBatchWriteEntry() {
    }

    public AssetBatchWriteEntry(String url) {
        this.url = url;
    }

}
