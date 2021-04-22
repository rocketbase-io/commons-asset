package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.ColorPalette;
import io.rocketbase.commons.dto.asset.Resolution;
import io.rocketbase.commons.util.Nulls;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@Entity
@Table(name = "co_asset",
        indexes = {
                @Index(name = "idx_asset_reference_hash", columnList = "reference_hash"),
                @Index(name = "idx_asset_context", columnList = "context"),
                @Index(name = "idx_asset_systemrefid", columnList = "system_ref_id")
        }
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetJpaEntity implements AssetEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Nullable
    @Column(name = "system_ref_id", length = 100)
    private String systemRefId;

    /**
     * relative path of storage
     */
    @Column(name = "url_path", length = 500)
    private String urlPath;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 10, nullable = false)
    private AssetType type;

    /**
     * allows to store individual grouping for assets to find all picture of a flexible type<br>
     * for example all avatar images or backgrounds...
     */
    @Column(name = "context", length = 100)
    private String context;

    @NotNull
    @Column(name = "created", nullable = false)
    private Instant created;

    @Column(name = "modified_by", length = 36)
    private String modifiedBy;

    @NotNull
    @Column(name = "modified", nullable = false)
    private Instant modified;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "file_size", nullable = false)
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
    @Column(name = "reference_hash", length = 64)
    private String referenceHash;

    /**
     * only filled in case of batch downloaded image
     */
    @Column(name = "reference_url", length = 2000)
    private String referenceUrl;

    @Lob
    @Column(name = "lqip")
    private String lqip;

    @ElementCollection
    @CollectionTable(
            name = "co_asset_keyvalue",
            joinColumns = @JoinColumn(name = "asset_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_asset_keyvalue__asset")),
            indexes = {
                    @Index(name = "idx_asset_keyvalue_asset", columnList = "asset_id"),
                    @Index(name = "idx_asset_keyvalue_value", columnList = "field_key, field_value"),
            }
    )
    @MapKeyColumn(name = "field_key", length = 50)
    @Column(name = "field_value", length = 255, nullable = false)
    @Builder.Default
    private Map<String, String> keyValueMap = new HashMap<>();

    @Column(name = "eol")
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

    @Override
    public void setKeyValues(Map<String, String> keyValues) {
        this.keyValueMap = keyValues;
    }

    public AssetJpaEntity(String id) {
        this.id = id;
    }
}