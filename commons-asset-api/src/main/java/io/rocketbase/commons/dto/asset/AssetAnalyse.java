package io.rocketbase.commons.dto.asset;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.ConstructorProperties;
import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssetAnalyse extends AssetMeta {

    private AssetType type;

    /**
     * in case of enabeled lqip contain base64 image preview
     */
    private String lqip;

    @JsonCreator
    @ConstructorProperties({"created", "originalFilename", "fileSize", "resolution", "colorPalette", "referenceUrl", "type", "lqip"})
    @Builder(builderMethodName = "builderAnalyse")
    public AssetAnalyse(Instant created, String originalFilename, long fileSize, Resolution resolution, ColorPalette colorPalette, String referenceUrl, AssetType type, String lqip) {
        super(created, originalFilename, fileSize, resolution, colorPalette, referenceUrl);
        this.type = type;
        this.lqip = lqip;
    }

    public AssetAnalyse(AssetMeta meta, AssetType type, String lqip) {
        super(meta.getCreated(), meta.getOriginalFilename(), meta.getFileSize(), meta.getResolution(), meta.getColorPalette(), meta.getReferenceUrl());
        this.type = type;
        this.lqip = lqip;
    }

    @Override
    public String toString() {
        return "AssetAnalyse(type=" + getType() + ", created=" + this.getCreated() + ", originalFilename=" + this.getOriginalFilename() + ", fileSize=" + this.getFileSize() + ", resolution=" + this.getResolution() + ", colorPalette=" + this.getColorPalette() + ", referenceUrl=" + this.getReferenceUrl() + ")";
    }
}
