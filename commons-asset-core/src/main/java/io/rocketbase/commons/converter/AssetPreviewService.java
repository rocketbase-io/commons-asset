package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.asset.PreviewSize;

public interface AssetPreviewService {

    String getPreviewUrl(String id, String urlPath, PreviewSize size);

}
