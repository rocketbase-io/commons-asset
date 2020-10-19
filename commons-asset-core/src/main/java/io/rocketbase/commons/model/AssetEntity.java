package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.asset.*;
import org.springframework.data.annotation.Transient;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Map;

public interface AssetEntity extends AssetReference {

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

    default boolean hasKeyValue(String key) {
        return getKeyValues() != null && key != null && getKeyValues().containsKey(key.toLowerCase());
    }

    default String getKeyValue(String key) {
        return getKeyValues() != null && key != null ? getKeyValues().getOrDefault(key.toLowerCase(), null) : null;
    }

    /**
     * @param key   will get stored with lowercase<br>
     *              max length of 50 characters<br>
     *              key with _ as prefix will not get displayed in REST_API
     * @param value max length of 4000 characters
     * @return itself for fluent api
     */
    default AssetEntity addKeyValue(String key, String value) {
        checkKeyValue(key, value);
        getKeyValues().put(key.toLowerCase(), value);
        return this;
    }

    default void removeKeyValue(String key) {
        getKeyValues().remove(key.toLowerCase());
    }

    default void checkKeyValue(String key, String value) {
        Assert.hasLength(key, "Key must not be empty");
        Assert.state(key.length() <= 50, "Key is too long - at least 50 chars");
        Assert.hasLength(value, "Value must not be empty");
        Assert.state(value.length() <= 4000, "Value is too long - at least 4000 chars");
    }

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
