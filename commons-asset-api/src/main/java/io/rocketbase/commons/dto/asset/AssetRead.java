package io.rocketbase.commons.dto.asset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.beans.ConstructorProperties;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssetRead extends AssetReference {

    private AssetPreviews previews;

    /**
     * optional property to receive the downloadUrl
     */
    private String download;

    private Map<String, String> keyValues;

    public AssetRead() {
    }

    @JsonCreator
    @ConstructorProperties({"id", "systemRefId", "urlPath", "type", "context", "meta", "previews", "download", "keyValues"})
    @Builder(builderMethodName = "builderRead")
    public AssetRead(@NotNull String id, String systemRefId, String urlPath, AssetType type, String context, AssetMeta meta, AssetPreviews previews, String download, Map<String, String> keyValues) {
        super(id, systemRefId, urlPath, type, context, meta);
        setPreviews(previews);
        setDownload(download);
        setKeyValues(keyValues);
    }

    public String toString() {
        return "AssetRead(id=" + this.getId() + ", systemRefId=" + this.getSystemRefId() + ", urlPath=" + this.getUrlPath() + ", type=" + this.getType() + ", context=" + this.getContext() + ", meta=" + this.getMeta() + ", previews=" + this.getPreviews() + ", download=" + this.getDownload() + ")";
    }


    @JsonIgnore
    public AssetReference toReference() {
        return (AssetReference) this;
    }
}
