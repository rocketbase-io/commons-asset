package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.AssetApiProperties;

public class DefaultAssetPreviewService extends AbstractAssetPreviewService {

    public DefaultAssetPreviewService(AssetApiProperties assetApiProperties) {
        super(assetApiProperties);
    }

    @Override
    protected String getBaseUrl() {
        return assetApiProperties.getBaseUrl();
    }

}
