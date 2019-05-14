package io.rocketbase.commons.dto.asset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.beans.ConstructorProperties;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssetRead extends AssetReference {

    private AssetPreviews previews;

    public AssetRead() {
    }

    @JsonCreator
    @ConstructorProperties({"id", "systemRefId", "urlPath", "type", "meta", "previews"})
    @Builder(builderMethodName = "builderRead")
    public AssetRead(@NotNull String id, String systemRefId, String urlPath, AssetType type, AssetMeta meta, AssetPreviews previews) {
        super(id, systemRefId, urlPath, type, meta);
        setPreviews(previews);
    }

    public String toString() {
        return "AssetRead(id=" + this.getId() + ", systemRefId=" + this.getSystemRefId() + ", urlPath=" + this.getUrlPath() + ", type=" + this.getType() + ", meta=" + this.getMeta() + ", previews=" + this.getPreviews() + ")";
    }

    @JsonIgnore
    public AssetReference toReference() {
        return (AssetReference) this;
    }
}
