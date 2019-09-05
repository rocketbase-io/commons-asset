package io.rocketbase.commons.converter;

import com.squareup.pollexor.Thumbor;
import io.rocketbase.commons.config.ApiProperties;
import io.rocketbase.commons.config.ThumborProperties;
import io.rocketbase.commons.dto.asset.AssetReference;
import io.rocketbase.commons.dto.asset.PreviewSize;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractAssetPreviewService implements AssetPreviewService {

    protected final ThumborProperties thumborProperties;

    protected final ApiProperties apiProperties;

    protected final boolean localEndpoint;

    protected Thumbor thumbor;

    protected abstract String getBaseUrl();

    public String getPreviewUrl(AssetReference assetReference, PreviewSize size) {
        if (localEndpoint) {
            String baseUrl = getBaseUrl();
            if (baseUrl == null) {
                baseUrl = "";
            }
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            return baseUrl + apiProperties.getPath() + "/" + assetReference.getId() + "/" + size.name().toLowerCase();
        } else {
            return getThumbor().buildImage(assetReference.getUrlPath())
                    .resize(size.getMaxWidth(), size.getMaxHeight())
                    .fitIn()
                    .toUrl();
        }
    }

    protected Thumbor getThumbor() {
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
