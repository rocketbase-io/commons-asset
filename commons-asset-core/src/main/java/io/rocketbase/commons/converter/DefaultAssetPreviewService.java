package io.rocketbase.commons.converter;

import com.squareup.pollexor.Thumbor;
import io.rocketbase.commons.config.ApiProperties;
import io.rocketbase.commons.config.ThumborProperties;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.service.MongoFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
public class DefaultAssetPreviewService implements AssetPreviewService {


    private final ThumborProperties thumborProperties;

    private final ApiProperties apiProperties;

    private final FileStorageService fileStorageService;

    private Thumbor thumbor;


    private boolean useLocalEndpoint() {
        return fileStorageService instanceof MongoFileStorageService;
    }


    protected String getBaseUrl() {
        try {
            return ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        } catch (Exception e) {
            return apiProperties.getBaseUrl();
        }
    }

    public String getPreviewUrl(String id, String urlPath, PreviewSize size) {
        if (useLocalEndpoint()) {
            String baseUrl = getBaseUrl();
            if (baseUrl == null) {
                baseUrl = "";
            }
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            return baseUrl + apiProperties.getPath() + "/" + id + "/" + size.name().toLowerCase();
        } else {
            return getThumbor().buildImage(urlPath)
                    .resize(size.getMaxWidth(), size.getMaxHeight())
                    .fitIn()
                    .toUrl();
        }
    }

    private Thumbor getThumbor() {
        if (thumbor == null) {
            String thumborKey = thumborProperties.getKey();
            if (thumborKey.isEmpty()) {
                thumbor = Thumbor.create(thumborProperties.getHost());
            } else {
                thumbor = Thumbor.create(thumborProperties.getHost(), thumborKey);
            }
        }
        return thumbor;
    }

}
