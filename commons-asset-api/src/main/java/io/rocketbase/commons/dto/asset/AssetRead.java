package io.rocketbase.commons.dto.asset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.rocketbase.commons.model.HasKeyValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import javax.annotation.Nullable;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "representation of an AssetEntity with all it's detail.")
public class AssetRead extends DefaultAssetReference implements HasKeyValue {

    private AssetPreviews previews;

    /**
     * optional property to receive the downloadUrl
     */
    @Nullable
    @Schema(description = "optional property to receive the downloadUrl")
    private String download;

    @Nullable
    @Schema(description = "optional keyValuePair that could have been stored related to the asset")
    private Map<String, String> keyValues;

    @Nullable
    @Schema(description = "date after that the asset could be deleted")
    private Instant eol;

    private String modifiedBy;

    private Instant modified;

    @Builder(builderMethodName = "builderRead")
    public AssetRead(String id, String systemRefId, String urlPath, AssetType type, String context, AssetMeta meta, String lqip, AssetPreviews previews, String download, Map<String, String> keyValues, Instant eol, String modifiedBy, Instant modified) {
        super(id, systemRefId, urlPath, type, context, meta, lqip);
        setPreviews(previews);
        setDownload(download);
        setKeyValues(keyValues);
        setEol(eol);
        setModifiedBy(modifiedBy);
        setModified(modified);
    }

    public String toString() {
        return "AssetRead(id=" + this.getId() + ", systemRefId=" + this.getSystemRefId() + ", urlPath=" + this.getUrlPath() + ", type=" + this.getType() + ", context=" + this.getContext() + ", meta=" + this.getMeta() + ", previews=" + this.getPreviews() + ", download=" + this.getDownload() + ")";
    }

    @JsonIgnore
    public AssetReference toReference() {
        return new DefaultAssetReference(this);
    }
}
