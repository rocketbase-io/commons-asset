package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.AssetUploadMeta;
import io.rocketbase.commons.util.Nulls;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface AssetTypeFilterService {

    boolean isAllowed(AssetType type, AssetUploadDetail detail);

    List<AssetType> getAllowedAssetTypes();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    class AssetUploadDetail implements AssetUploadMeta {

        private File file;
        private String originalFilename;
        private long size;
        private String systemRefId;
        private String context;
        private String referenceUrl;
        private Map<String, String> keyValues;
        private Instant eol;

        public AssetUploadDetail(File file, String originalFilename, long size, String referenceUrl, AssetUploadMeta uploadMeta) {
            this.file = file;
            this.originalFilename = originalFilename;
            this.size = size;
            this.referenceUrl = referenceUrl;
            systemRefId = Nulls.notNull(uploadMeta, AssetUploadMeta::getSystemRefId, null);
            context = Nulls.notNull(uploadMeta, AssetUploadMeta::getContext, null);
            keyValues = Nulls.notNull(uploadMeta, AssetUploadMeta::getKeyValues, null);
            eol = Nulls.notNull(uploadMeta, AssetUploadMeta::getEol, null);

        }
    }
}
