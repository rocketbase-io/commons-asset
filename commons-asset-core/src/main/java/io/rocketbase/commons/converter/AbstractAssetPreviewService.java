package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.dto.asset.AssetReference;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.util.Nulls;
import io.rocketbase.commons.util.UrlParts;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collection;

@RequiredArgsConstructor
public abstract class AbstractAssetPreviewService implements AssetPreviewService {

    public static final Collection<AssetType> SUPPORTED_ASSET_TYPES = Arrays.asList(AssetType.JPEG, AssetType.PNG, AssetType.GIF, AssetType.TIFF);

    protected final AssetApiProperties assetApiProperties;
    protected final FileStorageService fileStorageService;
    protected final boolean imageMagickEnabled;

    protected abstract String getBaseUrl();

    public String getPreviewUrl(AssetReference assetReference, PreviewSize size) {
        return UrlParts.removeEndsWithSlash(Nulls.notNull(getBaseUrl())) + assetApiProperties.getPath() + "/" + assetReference.getId() + "/" + size.name().toLowerCase();
    }

    @Override
    public String getDownloadUrl(AssetReference assetReference) {
        String downloadUrl = fileStorageService != null ? fileStorageService.getDownloadUrl(assetReference) : null;
        if (downloadUrl == null) {
            return UrlParts.removeEndsWithSlash(getBaseUrl()) + assetApiProperties.getPath() + "/" + assetReference.getId() + "/b";
        }
        return downloadUrl;
    }

    @Override
    public boolean isPreviewSupported(AssetType assetType) {
        return imageMagickEnabled ? assetType.isImage() : SUPPORTED_ASSET_TYPES.contains(assetType);
    }
}
