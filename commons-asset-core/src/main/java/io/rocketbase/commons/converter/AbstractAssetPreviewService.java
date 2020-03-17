package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.dto.asset.AssetReferenceType;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractAssetPreviewService implements AssetPreviewService {

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
}
