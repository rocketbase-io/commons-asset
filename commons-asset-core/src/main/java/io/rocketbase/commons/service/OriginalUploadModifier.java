package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetAnalyse;
import io.rocketbase.commons.dto.asset.AssetUploadMeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

public interface OriginalUploadModifier {

    Modification modifyUploadBeforeSave(AssetAnalyse analyseResult, File file, AssetUploadMeta uploadMeta);

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Modification {
        private AssetAnalyse analyse;
        private File file;
    }

}
