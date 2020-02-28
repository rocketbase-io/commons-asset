package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.dto.asset.AssetReferenceType;
import io.rocketbase.commons.dto.asset.PreviewSize;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractAssetPreviewService implements AssetPreviewService {

    protected final AssetApiProperties assetApiProperties;

    protected abstract String getBaseUrl();

    public String getPreviewUrl(AssetReferenceType assetReference, PreviewSize size) {
        String baseUrl = getBaseUrl();
        if (baseUrl == null) {
            baseUrl = "";
        }
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + assetApiProperties.getPath() + "/" + assetReference.getId() + "/" + size.name().toLowerCase();
    }

}
