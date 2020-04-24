package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetAnalyse;
import io.rocketbase.commons.dto.asset.AssetUploadMeta;

import java.io.File;

public class DefaultOriginalUploadModifier implements OriginalUploadModifier {

    @Override
    public Modification modifyUploadBeforeSave(AssetAnalyse analyse, File file, AssetUploadMeta uploadMeta) {
        return new Modification(analyse, file);
    }
}
