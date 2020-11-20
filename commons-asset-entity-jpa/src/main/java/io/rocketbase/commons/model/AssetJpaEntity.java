package io.rocketbase.commons.model;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.ColorPalette;
import io.rocketbase.commons.dto.asset.Resolution;
import io.rocketbase.commons.service.AssetJpaRepository;
import io.rocketbase.commons.util.Nulls;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Entity
@Table(name = "co_asset",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_asset_system_ref_if", columnNames = {"systemRefId"})
        },
        indexes = {
                @Index(name = "idx_asset_reference_hash", columnList = "referenceHash"),
                @Index(name = "idx_asset_context", columnList = "context"),
        }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetJpaEntity implements AssetEntity {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Size(max = 100)
    @Column(length = 100)
    private String systemRefId;

    /**
     * relative path of storage
     */
    @Size(max = 500)
    @Column(length = 500)
    private String urlPath;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(length = 10, nullable = false)
    private AssetType type;

    /**
     * allows to store individual grouping for assets to find all picture of a flexible type<br>
     * for example all avatar images or backgrounds...
     */
    @Size(max = 100)
    @Column(length = 100)
    private String context;

    @NotNull
    @Column(nullable = false)
    private Instant created;

    private String originalFilename;

    private long fileSize;

    /**
     * only filled in case of image asset
     */
    @Embedded
    private ResolutionEntity resolutionEntity;

    /**
     * only filled in case of image asset
     */
    @Embedded
    private ColorPaletteEntity colorPaletteEntity;

    /**
     * only filled in case of batch downloaded image
     */
    @Size(max = 64)
    @Column(length = 64)
    private String referenceHash;

    /**
     * only filled in case of batch downloaded image
     */
    @Size(max = 2000)
    @Column(length = 2000)
    private String referenceUrl;

    @Lob
    private String lqip;

    @OneToMany(mappedBy = "asset", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssetJpaKeyValueEntity> keyValues;

    @Nullable
    private Instant eol;

    @Override
    public Resolution getResolution() {
        return Nulls.notNull(resolutionEntity, ResolutionEntity::toApi, null);
    }

    @Override
    public void setResolution(Resolution resolution) {
        if (resolution == null) {
            this.resolutionEntity = null;
        } else {
            this.resolutionEntity = new ResolutionEntity(resolution.getWidth(), resolution.getHeight());
        }
    }

    @Override
    public ColorPalette getColorPalette() {
        return Nulls.notNull(colorPaletteEntity, ColorPaletteEntity::toApi, null);
    }

    @Override
    public void setColorPalette(ColorPalette colorPalette) {
        if (colorPalette == null) {
            this.colorPaletteEntity = null;
        } else {
            this.colorPaletteEntity = new ColorPaletteEntity(colorPalette.getPrimary(), colorPalette.getColors());
        }
    }

    @Override
    public Map<String, String> getKeyValues() {
        if (keyValues == null) {
            return Collections.emptyMap();
        }

        return ImmutableMap.copyOf(keyValues.stream()
                .collect(Collectors.toMap(AssetJpaKeyValueEntity::getFieldKey, AssetJpaKeyValueEntity::getFieldValue)));
    }

    @Override
    public AssetEntity addKeyValue(String key, String value) {
        if (getKeyValueEntities() == null) {
            setKeyValues(new ArrayList<>());
        }
        findKeyValue(key).ifPresent(v -> v.setFieldValue(value));
        return this;
    }

    @Override
    public void removeKeyValue(String key) {
        findKeyValue(key).ifPresent(v -> getKeyValueEntities().remove(v));
    }

    public Optional<AssetJpaKeyValueEntity> findKeyValue(String key) {
        if (getKeyValueEntities() != null && key != null) {
            return getKeyValueEntities().stream().filter(v -> v.getFieldKey().equals(key))
                    .findFirst();
        }
        return Optional.empty();
    }

    public List<AssetJpaKeyValueEntity> getKeyValueEntities() {
        return keyValues;
    }

    @PrePersist
    @PreUpdate
    public void onPrePersistUpdate() {
        if (getId() == null) {
            setId(UUID.randomUUID().toString());
        }
        if (!StringUtils.isEmpty(getReferenceUrl())) {
            setReferenceHash(AssetJpaRepository.hashValue(getReferenceUrl()));
        }
        if (created == null) {
            created = Instant.now();
        }
    }

    public AssetJpaEntity(String id) {
        this.id = id;
    }
}
