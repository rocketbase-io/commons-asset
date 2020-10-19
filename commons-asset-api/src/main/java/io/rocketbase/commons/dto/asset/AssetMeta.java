package io.rocketbase.commons.dto.asset;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.lang.Nullable;
import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetMeta implements Serializable {

    private Instant created;

    private String originalFilename;

    private long fileSize;

    /**
     * only filled in case of image asset
     */
    @Nullable
    private Resolution resolution;

    /**
     * only filled in case of image asset
     */
    @Nullable
    private ColorPalette colorPalette;

    /**
     * only filled in case of batch downloaded image
     */
    @Nullable
    private String referenceUrl;

    public AssetMeta(AssetMeta other) {
            this.created = other.created;
            this.originalFilename = other.originalFilename;
            this.fileSize = other.fileSize;
            this.resolution = other.resolution != null ? new Resolution(other.resolution) : null;
            this.colorPalette = other.colorPalette != null ? new ColorPalette(other.colorPalette) : null;
            this.referenceUrl = other.referenceUrl;
    }
}
