package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.asset.AssetMeta;
import io.rocketbase.commons.dto.asset.AssetReference;
import org.springframework.data.annotation.Transient;

public interface AssetEntity {

    String getId();

    void setId(String id);

    String getSystemRefId();

    void setSystemRefId(String systemRefId);

    String getUrlPath();

    void setUrlPath(String urlPath);

    io.rocketbase.commons.dto.asset.AssetType getType();

    void setType(io.rocketbase.commons.dto.asset.AssetType type);

    String getContext();

    void setContext(String context);

    java.time.LocalDateTime getCreated();

    void setCreated(java.time.LocalDateTime created);

    String getOriginalFilename();

    void setOriginalFilename(String originalFilename);

    long getFileSize();

    void setFileSize(long fileSize);

    io.rocketbase.commons.dto.asset.Resolution getResolution();

    void setResolution(io.rocketbase.commons.dto.asset.Resolution resolution);

    io.rocketbase.commons.dto.asset.ColorPalette getColorPalette();

    void setColorPalette(io.rocketbase.commons.dto.asset.ColorPalette colorPalette);

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
