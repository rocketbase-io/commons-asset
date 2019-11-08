package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.asset.AssetReference;
import io.rocketbase.commons.dto.asset.AssetReferenceType;
import io.rocketbase.commons.dto.asset.PreviewSize;

public interface AssetPreviewService {

    String getPreviewUrl(AssetReferenceType assetReference, PreviewSize size);

}
