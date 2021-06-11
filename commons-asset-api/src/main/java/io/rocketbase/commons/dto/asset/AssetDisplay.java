package io.rocketbase.commons.dto.asset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.annotation.Nullable;

/**
 * a short representation of {@link AssetRead} in order to reduce response values for rendering an asset within an application
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "a short representation of AssetRead in order to reduce response values for rendering an asset within an application")
public class AssetDisplay {

    /**
     * unique id of asset
     */
    @Schema(description = "unique id of asset")
    private String id;

    /**
     * type of asset
     */
    @Schema(description = "type of asset")
    private AssetType type;

    /**
     * additional information to asset
     */
    @Schema(description = "additional information to asset")
    private AssetMeta meta;

    /**
     * values to render image<br>
     * in case of documents/videos etc this will be null
     */
    @Nullable
    @Schema(description = "values to render image. in case of documents/videos etc this will be null")
    private ResponsiveImage image;

    /**
     * url to download original file<br>
     * in case of disabled download this value could be null
     */
    @Nullable
    @Schema(description = "url to download original file. in case of disabled download this value could be null")
    private String download;

    /**
     * Low Quality Image Placeholder (LQIP) that is a base64 preview in ultra low-res + quality
     */
    @Nullable
    @Schema(description = "Low Quality Image Placeholder (LQIP) that is a base64 preview in ultra low-res + quality")
    private String lqip;
}
