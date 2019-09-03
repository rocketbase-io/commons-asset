package io.rocketbase.commons.dto.asset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * used to store reference in db
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetReference implements Serializable {

    /**
     * reference to asset in asset collection
     */
    @NotNull
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

}
