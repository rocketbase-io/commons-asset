package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.ApiProperties;
import io.rocketbase.commons.config.ThumborProperties;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class ServletAssetPreviewService extends AbstractAssetPreviewService {

    public ServletAssetPreviewService(ThumborProperties thumborProperties, ApiProperties apiProperties, boolean localEndpoint) {
        super(thumborProperties, apiProperties, localEndpoint);
    }

    @Override
    protected String getBaseUrl() {
        try {
            return ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        } catch (Exception e) {
            return apiProperties.getBaseUrl();
        }
    }
}
