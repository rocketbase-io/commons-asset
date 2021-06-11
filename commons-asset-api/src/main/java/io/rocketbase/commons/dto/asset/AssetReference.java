package io.rocketbase.commons.dto.asset;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.annotation.Nullable;

import java.io.Serializable;

/**
 * used to store reference in db or elsewhere<br>
 * could be converted to AssetRead without database access
 */
public interface AssetReference extends Serializable {

    /**
     * reference to asset in asset collection
     */
    @Schema(description = "reference to asset in asset collection")
    String getId();

    /**
     * optional foreign id of other system
     */
    @Nullable
    @Schema(description = "optional foreign id of other system")
    String getSystemRefId();

    /**
     * relative path within storage
     */
    @Schema(description = "relative path within storage")
    String getUrlPath();

    AssetType getType();

    /**
     * allows to store individual grouping for assets to find all picture of a flexible type<br>
     * for example all avatar images or backgrounds...
     */
    @Nullable
    @Schema(description = "allows to store individual grouping for assets to find all picture of a flexible type", example = "background")
    String getContext();

    /**
     * additional information to asset
     */
    @Schema(description = "additional information to asset")
    AssetMeta getMeta();


    /**
     * Low Quality Image Placeholder (LQIP) that is a base64 preview in ultra low-res + quality
     */
    @Nullable
    @Schema(description = "Low Quality Image Placeholder (LQIP) that is a base64 preview in ultra low-res + quality")
    String getLqip();
}
