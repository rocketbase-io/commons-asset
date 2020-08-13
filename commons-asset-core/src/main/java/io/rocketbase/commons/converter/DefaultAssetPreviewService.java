package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.service.FileStorageService;

public class DefaultAssetPreviewService extends AbstractAssetPreviewService {

    public DefaultAssetPreviewService(AssetApiProperties assetApiProperties, FileStorageService fileStorageService, boolean imageMagickEnabled) {
        super(assetApiProperties, fileStorageService, imageMagickEnabled);
    }

    @Override
    protected String getBaseUrl() {
        return assetApiProperties.getBaseUrl();
    }

}
