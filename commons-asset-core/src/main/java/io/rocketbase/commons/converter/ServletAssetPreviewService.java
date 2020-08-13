package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.util.Nulls;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

public class ServletAssetPreviewService extends AbstractAssetPreviewService {

    public ServletAssetPreviewService(AssetApiProperties assetApiProperties, FileStorageService fileStorageService, boolean imageMagickEnabled) {
        super(assetApiProperties, fileStorageService, imageMagickEnabled);
    }

    @Override
    protected String getBaseUrl() {
        try {
            ServletUriComponentsBuilder uriComponentsBuilder = ServletUriComponentsBuilder.fromCurrentContextPath();
            try {
                // in some cases uriComponentsBuilder will ignore ssl (checks for X-Forwarded-Ssl: on) ignore this behaviour...
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                String scheme = Nulls.notNull(request.getHeader("x-forwarded-proto"), request.getHeader("x-scheme"));
                if ("https".equalsIgnoreCase(scheme)) {
                    uriComponentsBuilder.scheme(scheme);
                }
            } catch (Exception e) {
            }
            return uriComponentsBuilder.toUriString();
        } catch (Exception e) {
            return assetApiProperties.getBaseUrl();
        }
    }
}
