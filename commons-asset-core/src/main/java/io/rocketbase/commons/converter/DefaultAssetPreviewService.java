package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.ApiProperties;
import io.rocketbase.commons.config.ThumborProperties;

public class DefaultAssetPreviewService extends AbstractAssetPreviewService {

    public DefaultAssetPreviewService(ThumborProperties thumborProperties, ApiProperties apiProperties, boolean localEndpoint) {
        super(thumborProperties, apiProperties, localEndpoint);
    }

    @Override
    protected String getBaseUrl() {
        return apiProperties.getBaseUrl();
    }

}
