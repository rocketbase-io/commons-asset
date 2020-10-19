package io.rocketbase.commons.dto.asset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * used to store reference in db or elsewhere<br>
 * could be converted to AssetRead without database access
 */
@JsonDeserialize(as = DefaultAssetReference.class)
public interface AssetReference extends Serializable {

    /**
     * reference to asset in asset collection
     */
    String getId();

    /**
     * optional foreign id of other system
     */
    @Nullable
    String getSystemRefId();

    /**
     * relative path of storage
     */
    String getUrlPath();

    AssetType getType();

    /**
     * allows to store individual grouping for assets to find all picture of a flexible type<br>
     * for example all avatar images or backgrounds...
     */
    @Nullable
    String getContext();

    /**
     * Low Quality Image Placeholder (LQIP) that is a base64 preview in ultra low-res + quality
     */
    AssetMeta getMeta();
}
