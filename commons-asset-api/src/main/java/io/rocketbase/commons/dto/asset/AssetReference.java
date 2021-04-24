package io.rocketbase.commons.dto.asset;

import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * used to store reference in db or elsewhere<br>
 * could be converted to AssetRead without database access
 */
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
     * additional information to asset
     */
    AssetMeta getMeta();


    /**
     * Low Quality Image Placeholder (LQIP) that is a base64 preview in ultra low-res + quality
     */
    @Nullable
    String getLqip();
}
