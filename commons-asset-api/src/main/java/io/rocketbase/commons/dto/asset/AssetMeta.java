package io.rocketbase.commons.dto.asset;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetMeta implements Serializable {

    private Instant created;

    private String originalFilename;

    private long fileSize;

    /**
     * only filled in case of image asset
     */
    @Nullable
    private Resolution resolution;

    /**
     * only filled in case of image asset
     */
    @Nullable
    private ColorPalette colorPalette;

    /**
     * only filled in case of batch downloaded image
     */
    @Nullable
    private String referenceUrl;
}
