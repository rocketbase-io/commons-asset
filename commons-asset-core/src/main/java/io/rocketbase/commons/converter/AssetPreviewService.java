package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.asset.AssetReference;
import io.rocketbase.commons.dto.asset.PreviewSize;

public interface AssetPreviewService {

    String getPreviewUrl(AssetReference assetReference, PreviewSize size);

}
