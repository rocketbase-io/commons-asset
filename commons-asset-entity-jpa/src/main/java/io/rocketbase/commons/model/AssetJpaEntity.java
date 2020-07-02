package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.ColorPalette;
import io.rocketbase.commons.dto.asset.Resolution;
import io.rocketbase.commons.util.Nulls;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "co_asset_keyvalue_pair",
            joinColumns = @JoinColumn(name = "asset_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_asset_keyvalue_pair", columnNames = {"asset_id", "field_key"}),
            indexes = @Index(name = "idx_asset_keyvalue_pair", columnList = "asset_id")
    )
    @MapKeyColumn(name = "field_key", length = 50)
    @Column(name = "field_value", length = 4000, nullable = false)
    @Builder.Default
    private Map<String, String> keyValueMap = new HashMap<>();

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
        return keyValueMap;
    }

}
