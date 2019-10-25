package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.asset.*;
import org.springframework.data.annotation.Transient;

import java.time.Instant;

public interface AssetEntity {

    String getId();

    void setId(String id);

    String getSystemRefId();

    void setSystemRefId(String systemRefId);

    String getUrlPath();

    void setUrlPath(String urlPath);

    AssetType getType();

    void setType(AssetType type);

    String getContext();

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
                .meta(AssetMeta.builder()
                        .created(getCreated())
                        .fileSize(getFileSize())
                        .originalFilename(getOriginalFilename())
                        .resolution(getResolution())
                        .colorPalette(getColorPalette())
                        .referenceUrl(getReferenceUrl())
                        .build())
                .build();
    }

}
