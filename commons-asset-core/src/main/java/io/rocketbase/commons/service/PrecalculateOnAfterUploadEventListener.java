package io.rocketbase.commons.service;

import com.google.common.base.Stopwatch;
import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.event.AssetAfterUploadEvent;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.handler.AssetHandler;
import io.rocketbase.commons.service.handler.PreviewConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class PrecalculateOnAfterUploadEventListener implements ApplicationListener<AssetAfterUploadEvent> {

    private final AssetApiProperties assetApiProperties;
    private final AssetHandler assetHandler;
    private final FileStorageService fileStorageService;

    @Override
    public void onApplicationEvent(AssetAfterUploadEvent assetAfterUploadEvent) {
        if (assetApiProperties.isPrecalculate()) {
            AssetEntity entity = assetAfterUploadEvent.getAssetEntity();
            if (assetHandler.isPreviewSupported(entity.getType())) {
                Stopwatch stopwatch = Stopwatch.createStarted();
                assetApiProperties.getPreviewSizes()
                        .forEach(previewSize -> {
                            if (entity.getResolution() == null || entity.getResolution().isBiggerThan(previewSize)) {
                                File previewFile = assetHandler.getPreview(entity.getType(),
                                        assetAfterUploadEvent.getModification().getFile(),
                                        PreviewConfig.builder()
                                                .previewSize(previewSize)
                                                .build());
                                fileStorageService.storePreview(entity, previewFile, previewSize);
                            } else {
                                fileStorageService.storePreview(entity, assetAfterUploadEvent.getModification().getFile(), previewSize);
                            }
                        });
                if (log.isDebugEnabled()) {
                    log.debug("precalculated asset previews took: {} ms for sizes: {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), assetApiProperties.getPreviewSizes());
                }
            }
        }
    }
}
