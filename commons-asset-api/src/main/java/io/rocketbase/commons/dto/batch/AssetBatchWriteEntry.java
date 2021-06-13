package io.rocketbase.commons.dto.batch;


import io.rocketbase.commons.dto.asset.AssetUploadMeta;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.lang.Nullable;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;


/**
 * detailed instruction for each url
 */
@Data
@AllArgsConstructor
@Schema(description = "detailed instruction for each url")
public class AssetBatchWriteEntry implements AssetUploadMeta {

    /**
     * full qualified url to the asset that should been analysed/stored
     */
    @NotNull
    @Schema(description = "full qualified url to the asset that should been analysed/stored", required = true)
    private String url;

    /**
     * optional foreign system reference id  that will get stored to the entity
     */
    @Nullable
    @Schema(description = "optional foreign system reference id  that will get stored to the entity")
    private String systemRefId;

    /**
     * optional context that will get stored to the entity
     */
    @Nullable
    @Schema(description = "optional context that will get stored to the entity")
    private String context;

    /**
     * optional keyValues that will get stored to the entity
     */
    @Nullable
    @Schema(description = "optional keyValues that will get stored to the entity")
    private Map<String, String> keyValues;

    /**
     * optional eol that will get stored to the entity
     */
    @Nullable
    @Schema(description = "optional eol that will get stored to the entity")
    private Instant eol;

    public AssetBatchWriteEntry() {
    }

    public AssetBatchWriteEntry(String url) {
        this.url = url;
    }

}
