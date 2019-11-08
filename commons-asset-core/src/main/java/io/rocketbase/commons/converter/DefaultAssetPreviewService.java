package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.ApiProperties;

public class DefaultAssetPreviewService extends AbstractAssetPreviewService {

    public DefaultAssetPreviewService(ApiProperties apiProperties) {
        super(apiProperties);
    }

    @Override
    protected String getBaseUrl() {
        return apiProperties.getBaseUrl();
    }

}
