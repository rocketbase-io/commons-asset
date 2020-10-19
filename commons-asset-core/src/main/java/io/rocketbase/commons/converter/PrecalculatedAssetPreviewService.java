package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.converter.AbstractAssetPreviewService;
import io.rocketbase.commons.dto.asset.AssetReference;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.util.UrlParts;

public class PrecalculatedAssetPreviewService extends AbstractAssetPreviewService {


    public PrecalculatedAssetPreviewService(AssetApiProperties assetApiProperties, boolean imageMagickEnabled,  FileStorageService fileStorageService) {
        super(assetApiProperties, fileStorageService, imageMagickEnabled);
    }

    @Override
    protected String getBaseUrl() {
        return null;
    }

    @Override
    public String getPreviewUrl(AssetReference assetReference, PreviewSize size) {
        String downloadUrl = fileStorageService != null ? fileStorageService.getDownloadPreviewUrl(assetReference, size) : null;
        if (downloadUrl == null) {
            return UrlParts.removeEndsWithSlash(assetApiProperties.getBaseUrl()) + assetApiProperties.getPath() + "/" + assetReference.getId() + "/" + size.name().toLowerCase();
        }
        return downloadUrl;
    }
}
