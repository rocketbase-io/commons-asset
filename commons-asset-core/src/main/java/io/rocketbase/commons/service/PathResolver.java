package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetReferenceType;

public interface PathResolver {

    /**
     * custom implementation to allow different path layout within a bucket<br>
     * default layout looks like d/3/c/ when id of entity is 5a1d60dcf19aec0001815d3c
     *
     * @param assetReference current version of {@link AssetReferenceType} that is processed within {@link S3FileStoreService}
     * @return layout of path
     */
    String resolvePath(AssetReferenceType assetReference);
}
