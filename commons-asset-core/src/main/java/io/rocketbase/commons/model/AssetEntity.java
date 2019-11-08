package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.asset.*;
import org.springframework.data.annotation.Transient;

import java.time.Instant;

public interface AssetEntity extends AssetReferenceType {

    void setId(String id);

    void setSystemRefId(String systemRefId);

    void setUrlPath(String urlPath);

    void setType(AssetType type);

    void setContext(String context);

    Instant getCreated();

    void setCreated(Instant created);

    String getOriginalFilename();

    void setOriginalFilename(String originalFilename);

    long getFileSize();

    void setFileSize(long fileSize);

    Resolution getResolution();

    void setResolution(Resolution resolution);

    ColorPalette getColorPalette();

    void setColorPalette(ColorPalette colorPalette);

    String getReferenceUrl();

    void setReferenceUrl(String referenceUrl);

    @Transient
    default AssetReference toReference() {
        return AssetReference.builder()
                .id(getId())
                .systemRefId(getSystemRefId())
                .urlPath(getUrlPath())
                .type(getType())
                .meta(getMeta())
                .build();
    }

    @Transient
    default AssetMeta getMeta() {
        return AssetMeta.builder()
                .created(getCreated())
                .originalFilename(getOriginalFilename())
                .fileSize(getFileSize())
                .resolution(getResolution())
                .colorPalette(getColorPalette())
                .referenceUrl(getReferenceUrl())
                .build();
    }

}
