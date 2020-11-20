package io.rocketbase.commons.model;


import io.rocketbase.commons.model.id.AssetJpaKeyValueId;
import io.rocketbase.commons.service.AssetJpaRepository;
import io.rocketbase.commons.util.Nulls;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "co_asset_keyvalue",
        indexes = {
                @Index(name = "idx_asset_keyvalue_asset", columnList = "asset_id"),
                @Index(name = "idx_asset_keyvalue_key", columnList = "field_key"),
                @Index(name = "idx_asset_keyvalue_keyhash", columnList = "field_key, field_value_hash")
        }
)
@IdClass(AssetJpaKeyValueId.class)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetJpaKeyValueJpaEntity implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id")
    private AssetJpaEntity asset;

    @Id
    @NotNull
    @Size(max = 50)
    @Column(name = "field_key", length = 50, nullable = false)
    private String fieldKey;

    @NotNull
    @Lob
    @Column(name = "field_value", nullable = false)
    private String fieldValue;

    @Size(max = 64)
    @Column(name = "field_value_hash", nullable = false, length = 64)
    private String fieldValueHash;

    @LastModifiedDate
    private Instant lastUpdate;

    public AssetJpaKeyValueJpaEntity(AssetJpaEntity asset, String fieldKey, String fieldValue) {
        this.asset = asset;
        this.fieldKey = fieldKey;
        this.fieldValue = fieldValue;
    }

    @PrePersist
    @PreUpdate
    public void onPrePersistUpdate() {
        String oldHash = getFieldValueHash();
        if (getFieldValue() != null) {
            setFieldValueHash(AssetJpaRepository.hashValue(getFieldValue().toLowerCase()));
        }
        if (!Nulls.notNull(getFieldValueHash()).equals(oldHash)) {
            setLastUpdate(Instant.now());
        }
    }

}
