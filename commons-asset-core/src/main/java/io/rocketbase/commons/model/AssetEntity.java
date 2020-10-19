package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.asset.*;
import org.springframework.data.annotation.Transient;

import java.time.Instant;
import java.util.Map;

public interface AssetEntity extends AssetReference, EntityWithKeyValue<AssetEntity> {

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

    String getLqip();

    void setLqip(String lqip);

    Map<String, String> getKeyValues();

    void setEol(Instant eol);

    Instant getEol();

    @Transient
    default AssetReference toReference() {
        return DefaultAssetReference.builder()
                .id(getId())
                .systemRefId(getSystemRefId())
                .context(getContext())
                .urlPath(getUrlPath())
                .type(getType())
                .meta(getMeta())
                .lqip(getLqip())
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

    @Transient
    default boolean isEol() {
        return getEol() != null ? getEol().isBefore(Instant.now()) : false;
    }


}
