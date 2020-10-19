package io.rocketbase.commons.dto.asset;

import lombok.*;

import javax.annotation.Nullable;


/**
 * used to store reference in db
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "lqip")
public class DefaultAssetReference implements AssetReference {

    /**
     * reference to asset in asset collection
     */
    private String id;

    /**
     * optional foreign id of other system
     */
    @Nullable
    private String systemRefId;

    /**
     * relative path of storage
     */
    private String urlPath;

    private AssetType type;

    /**
     * allows to store individual grouping for assets to find all picture of a flexible type<br>
     * for example all avatar images or backgrounds...
     */
    @Nullable
    private String context;

    private AssetMeta meta;

    /**
     * Low Quality Image Placeholder (LQIP) that is a base64 preview in ultra low-res + quality
     */
    @Nullable
    private String lqip;

    public DefaultAssetReference(AssetReference other) {
        this.id = other.getId();
        this.systemRefId = other.getSystemRefId();
        this.urlPath = other.getUrlPath();
        this.type = other.getType();
        this.context = other.getContext();
        this.meta = other.getMeta() != null ? new AssetMeta(other.getMeta()) : null;
    }
}
