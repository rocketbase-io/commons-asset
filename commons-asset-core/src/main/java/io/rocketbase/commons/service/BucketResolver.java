package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetReferenceType;
import io.rocketbase.commons.model.AssetEntity;

public interface BucketResolver {

    /**
     * custom implementation to allow support for multiple buckets.<br>
     * for example you can use context to decide which bucket to use
     *
     * @param assetReference current version of {@link AssetReferenceType} that is processed within {@link S3FileStoreService}
     * @return name of the bucket to use
     */
    String resolveBucketName(AssetReferenceType assetReference);
}
