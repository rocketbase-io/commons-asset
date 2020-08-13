package io.rocketbase.commons.service;

import com.google.common.base.Stopwatch;
import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.event.AssetUploadEvent;
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
public class PrecalculateOnAssetUploadEventListener implements ApplicationListener<AssetUploadEvent> {

    private final AssetApiProperties assetApiProperties;
    private final AssetHandler assetHandler;
    private final FileStorageService fileStorageService;

    @Override
    public void onApplicationEvent(AssetUploadEvent assetUploadEvent) {
        if (assetApiProperties.isPrecalculate()) {
            AssetEntity entity = assetUploadEvent.getAssetEntity();
            if (assetHandler.isPreviewSupported(entity.getType())) {
                Stopwatch stopwatch = Stopwatch.createStarted();
                assetApiProperties.getPreviewSizes()
                        .forEach(s -> {
                            File previewFile = assetHandler.getPreview(entity.getType(),
                                    assetUploadEvent.getModification().getFile(),
                                    PreviewConfig.builder()
                                            .previewSize(s)
                                            .build());
                            fileStorageService.storePreview(entity, previewFile, s);
                        });
                if (log.isDebugEnabled()) {
                    log.debug("precalculated asset previews took: {} ms for sizes: {}", stopwatch.elapsed(TimeUnit.MILLISECONDS), assetApiProperties.getPreviewSizes());
                }
            }
        }
    }
}
