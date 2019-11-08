package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.ApiProperties;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class ServletAssetPreviewService extends AbstractAssetPreviewService {

    public ServletAssetPreviewService(ApiProperties apiProperties) {
        super(apiProperties);
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
