package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.ColorPalette;
import io.rocketbase.commons.dto.asset.Resolution;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Document(collection = "assets")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetMongoEntity implements AssetEntity {

    @Id
    private String id;

    /**
     * optional foreign id of other system
     */
    @Indexed
    private String systemRefId;

    /**
     * relative path of storage
     */
    private String urlPath;

    private AssetType type;

    /**
     * allows to store individual grouping for assets to find all picture of a flexible type<br>
     * for example all avatar images or backgrounds...
     */
    @Indexed
    private String context;

    @NotNull
    @CreatedDate
    private Instant created;

    @LastModifiedBy
    private String modifiedBy;

    @NotNull
    @LastModifiedDate
    private Instant modified;

    private String originalFilename;

    private long fileSize;

    /**
     * only filled in case of image asset
     */
    private Resolution resolution;

    /**
     * only filled in case of image asset
     */
    private ColorPalette colorPalette;

    /**
     * only filled in case of batch downloaded image
     */
    @Indexed
    private String referenceUrl;

    private String lqip;

    @Nullable
    private Instant eol;

    @Builder.Default
    private Map<String, String> keyValueMap = new HashMap<>();

    @Override
    public Map<String, String> getKeyValues() {
        return keyValueMap;
    }
}
