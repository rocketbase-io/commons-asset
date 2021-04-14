package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.asset.*;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Map;

public interface AssetEntity extends AssetReference, EntityWithKeyValue<AssetEntity> {

    void setId(String id);

    void setSystemRefId(@Size(max = 100) String systemRefId);

    void setUrlPath(@Size(max = 500) String urlPath);

    void setType(@NotNull AssetType type);

    void setContext(@Size(max = 100) String context);

    Instant getCreated();

    void setCreated(Instant created);

    Instant getModified();

    String getModifiedBy();

    String getOriginalFilename();

    void setOriginalFilename(@Size(max = 255) String originalFilename);

    long getFileSize();

    void setFileSize(long fileSize);

    Resolution getResolution();

    void setResolution(Resolution resolution);

    ColorPalette getColorPalette();

    void setColorPalette(ColorPalette colorPalette);

    String getReferenceUrl();

    void setReferenceUrl(@Size(max = 2000) String referenceUrl);

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
