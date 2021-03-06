package io.rocketbase.commons.service;

import io.rocketbase.commons.config.AssetShrinkProperties;
import io.rocketbase.commons.dto.asset.AssetAnalyse;
import io.rocketbase.commons.dto.asset.AssetUploadMeta;
import io.rocketbase.commons.dto.asset.Resolution;
import io.rocketbase.commons.service.handler.AssetHandler;
import io.rocketbase.commons.service.handler.PreviewConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
public class DefaultShrinkOriginalUploadModifier implements OriginalUploadModifier {

    final AssetShrinkProperties assetShrinkProperties;

    final AssetHandler assetHandler;

    @Override
    public Modification modifyUploadBeforeSave(AssetAnalyse analyse, File file, AssetUploadMeta uploadMeta) {
        if (analyse == null || analyse.getType() == null || !assetHandler.isPreviewSupported(analyse.getType())) {
            return new Modification(analyse, file);
        }
        Resolution resolution = Optional.ofNullable(analyse.getResolution()).orElseGet(() ->
                // in case resolution detection is disabled
                assetHandler.getResolution(analyse.getType(), file));
        if (resolution != null && analyse.getResolution().isBiggerThan(assetShrinkProperties.getMaxWidth(), assetShrinkProperties.getMaxHeight())) {
            try {
                File shrinkedFile = assetHandler.getPreview(analyse.getType(), file, PreviewConfig.builder()
                        .previewSize(assetShrinkProperties.getPreviewParameter())
                        .build());
                analyse.setResolution(analyse.getResolution().calculateWithAspectRatio(assetShrinkProperties.getMaxWidth(), assetShrinkProperties.getMaxHeight()));
                analyse.setFileSize(shrinkedFile.length());
                return new Modification(analyse, shrinkedFile);
            } catch (Exception e) {
                log.warn("couldn't shrink file {}", file.getPath());
            }
        }
        return new Modification(analyse, file);
    }
}
