package io.rocketbase.commons.dto.asset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * a short representation of {@link AssetRead} in order to reduce response values for rendering an asset within an application
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetDisplay {

    /**
     * unique id of asset
     */
    private String id;

    /**
     * type of asset
     */
    private AssetType type;

    /**
     * additional information to asset
     */
    private AssetMeta meta;

    /**
     * values to render image<br>
     * in case of documents/videos etc this will be null
     */
    @Nullable
    private ResponsiveImage image;

    /**
     * url to download original file<br>
     * in case of disabled download this value could be null
     */
    @Nullable
    private String download;

    /**
     * Low Quality Image Placeholder (LQIP) that is a base64 preview in ultra low-res + quality
     */
    @Nullable
    private String lqip;
}
