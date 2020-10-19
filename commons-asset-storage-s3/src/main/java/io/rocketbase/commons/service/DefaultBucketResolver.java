package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetReference;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultBucketResolver implements BucketResolver {

    private final String defaultBucket;

    @Override
    public String resolveBucketName(AssetReference assetReference) {
        return defaultBucket;
    }
}
