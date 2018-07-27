package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.asset.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "assets")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetEntity {

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

    private LocalDateTime created;

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

    @Transient
    public AssetReference toReference() {
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
                        .referenceUrl(getReferenceUrl())
                        .build())
                .build();
    }

}
