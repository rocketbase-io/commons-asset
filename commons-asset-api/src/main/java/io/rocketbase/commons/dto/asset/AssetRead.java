package io.rocketbase.commons.dto.asset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.rocketbase.commons.model.HasKeyValue;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AssetRead extends DefaultAssetReference implements HasKeyValue {

    private AssetPreviews previews;

    /**
     * optional property to receive the downloadUrl
     */
    @Nullable
    private String download;

    @Nullable
    private Map<String, String> keyValues;

    @Nullable
    private Instant eol;

    @Builder(builderMethodName = "builderRead")
    public AssetRead(@NotNull String id, String systemRefId, String urlPath, AssetType type, String context, AssetMeta meta, String lqip, AssetPreviews previews, String download, Map<String, String> keyValues, Instant eol) {
        super(id, systemRefId, urlPath, type, context, meta, lqip);
        setPreviews(previews);
        setDownload(download);
        setKeyValues(keyValues);
        setEol(eol);
    }

    public String toString() {
        return "AssetRead(id=" + this.getId() + ", systemRefId=" + this.getSystemRefId() + ", urlPath=" + this.getUrlPath() + ", type=" + this.getType() + ", context=" + this.getContext() + ", meta=" + this.getMeta() + ", previews=" + this.getPreviews() + ", download=" + this.getDownload() + ")";
    }

    @JsonIgnore
    public AssetReference toReference() {
        return new DefaultAssetReference(this);
    }
}
