package io.rocketbase.commons.dto.asset;


import io.rocketbase.commons.converter.BytesConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.annotation.Nullable;
import java.beans.Transient;
import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Attributes and detail around the asset content")
public class AssetMeta implements Serializable {

    private Instant created;

    @Schema(description = "name of the file during upload-process")
    private String originalFilename;

    @Schema(description = "original file size in bytes")
    private long fileSize;

    /**
     * only filled in case of image asset
     */
    @Nullable
    @Schema(description = "only filled in case of image asset")
    private Resolution resolution;

    /**
     * only filled in case of image asset
     */
    @Nullable
    @Schema(description = "only filled in case of image asset")
    private ColorPalette colorPalette;

    /**
     * only filled in case of batch downloaded image
     */
    @Nullable
    @Schema(description = "only filled in case of batch downloaded image")
    private String referenceUrl;

    public AssetMeta(AssetMeta other) {
            this.created = other.created;
            this.originalFilename = other.originalFilename;
            this.fileSize = other.fileSize;
            this.resolution = other.resolution != null ? new Resolution(other.resolution) : null;
            this.colorPalette = other.colorPalette != null ? new ColorPalette(other.colorPalette) : null;
            this.referenceUrl = other.referenceUrl;
    }

    @Transient
    @Schema(example = "1.5Mb")
    public String getFileSizeHumanReadable() {
        return BytesConverter.humanReadableBytes(fileSize);
    }
}
