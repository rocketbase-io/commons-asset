package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

public interface AssetTypeFilterService {

    boolean isAllowed(AssetType type, AssetUploadDetail detail);

    List<AssetType> getAllowedAssetTypes();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    class AssetUploadDetail {

        private File file;
        private String originalFilename;
        private long size;
        private String systemRefId;
        private String context;
        private String referenceUrl;
    }
}
