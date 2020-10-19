package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.asset.AssetReference;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.PreviewSize;

public interface AssetPreviewService {

    /**
     * return a list of supported preview types
     */
    boolean isPreviewSupported(AssetType assetType);

    String getPreviewUrl(AssetReference assetReference, PreviewSize size);

    String getDownloadUrl(AssetReference assetReference);

}
