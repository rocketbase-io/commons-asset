package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.dto.asset.AssetReferenceType;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collection;

@RequiredArgsConstructor
public abstract class AbstractAssetPreviewService implements AssetPreviewService {

    public static final Collection<AssetType> SUPPORTED_ASSET_TYPES = Arrays.asList(AssetType.JPEG, AssetType.PNG, AssetType.GIF, AssetType.TIFF);

    protected final AssetApiProperties assetApiProperties;

    protected abstract String getBaseUrl();

    public String getPreviewUrl(AssetReferenceType assetReference, PreviewSize size) {
        return removeEndsWithSlash(Nulls.notNull(getBaseUrl())) + assetApiProperties.getPath() + "/" + assetReference.getId() + "/" + size.name().toLowerCase();
    }

    protected String removeEndsWithSlash(String value) {
        if (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    @Override
    public String getDownloadUrl(AssetReferenceType assetReference) {
        return removeEndsWithSlash(Nulls.notNull(getBaseUrl())) + assetApiProperties.getPath() + "/" + assetReference.getId() + "/b";
    }

    @Override
    public boolean isPreviewSupported(AssetType assetType) {
        return SUPPORTED_ASSET_TYPES.contains(assetType);
    }
}
