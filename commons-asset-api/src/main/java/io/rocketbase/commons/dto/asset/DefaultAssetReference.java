package io.rocketbase.commons.dto.asset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;


/**
 * used to store reference in db
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "lqip")
@JsonDeserialize
public class DefaultAssetReference implements AssetReference {

    private String id;

    private String systemRefId;

    private String urlPath;

    private AssetType type;

    private String context;

    private AssetMeta meta;

    private String lqip;

    public DefaultAssetReference(AssetReference other) {
        this.id = other.getId();
        this.systemRefId = other.getSystemRefId();
        this.urlPath = other.getUrlPath();
        this.type = other.getType();
        this.context = other.getContext();
        this.meta = other.getMeta() != null ? new AssetMeta(other.getMeta()) : null;
        this.lqip = other.getLqip();
    }
}
