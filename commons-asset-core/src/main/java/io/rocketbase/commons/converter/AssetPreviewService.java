package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.asset.AssetReferenceType;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.PreviewSize;

public interface AssetPreviewService {

    /**
     * return a list of supported preview types
     */
    boolean isPreviewSupported(AssetType assetType);

    String getPreviewUrl(AssetReferenceType assetReference, PreviewSize size);

    String getDownloadUrl(AssetReferenceType assetReference);

}
