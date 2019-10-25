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

    @JsonCreator
    @ConstructorProperties({"created", "originalFilename", "fileSize", "resolution", "colorPalette", "referenceUrl", "type"})
    @Builder(builderMethodName = "builderAnalyse")
    public AssetAnalyse(Instant created, String originalFilename, long fileSize, Resolution resolution, ColorPalette colorPalette, String referenceUrl, AssetType type) {
        super(created, originalFilename, fileSize, resolution, colorPalette, referenceUrl);
        this.type = type;
    }

    @Override
    public String toString() {
        return "AssetAnalyse(type=" + getType() + ", created=" + this.getCreated() + ", originalFilename=" + this.getOriginalFilename() + ", fileSize=" + this.getFileSize() + ", resolution=" + this.getResolution() + ", colorPalette=" + this.getColorPalette() + ", referenceUrl=" + this.getReferenceUrl() + ")";
    }
}
