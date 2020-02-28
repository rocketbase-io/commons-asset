package io.rocketbase.commons.dto.asset;

import lombok.*;

import java.io.Serializable;


/**
 * used to store reference in db
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "lqip")
public class AssetReference implements Serializable, AssetReferenceType {

    /**
     * reference to asset in asset collection
     */
    private String id;

    /**
     * optional foreign id of other system
     */
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
    private String context;

    private AssetMeta meta;

    /**
     * Low Quality Image Placeholder (LQIP) that is a base64 preview in ultra low-res + quality
     */
    private String lqip;

}
