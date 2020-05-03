package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetReferenceType;
import io.rocketbase.commons.util.UrlParts;

public interface PathResolver {

    /**
     * custom implementation to allow different path layout within a bucket<br>
     * default layout looks like d/3/c/ when id of entity is 5a1d60dcf19aec0001815d3c
     *
     * @param assetReference current version of {@link AssetReferenceType} that is processed within S3FileStore + FsFileStore
     * @return layout of path
     */
    String resolvePath(AssetReferenceType assetReference);

    /**
     * custom implementation to allow different file-naming<br>
     * default is entityId . file-extension
     *
     * @param assetReference current version of {@link AssetReferenceType} that is processed within S3FileStore + FsFileStore
     * @return filename within given path
     */
    default String resolveFileName(AssetReferenceType assetReference) {
        return assetReference.getId().toLowerCase() + assetReference.getType().getFileExtensionForSuffix();
    }

    /**
     * get full path to file
     */
    default String getAbsolutePath(AssetReferenceType assetReference) {
        return UrlParts.ensureEndsWithSlash(resolvePath(assetReference)) + resolveFileName(assetReference);
    }
}
