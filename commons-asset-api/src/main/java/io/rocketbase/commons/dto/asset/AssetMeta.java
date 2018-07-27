package io.rocketbase.commons.dto.asset;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetMeta implements Serializable {

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
    private String referenceUrl;
}
