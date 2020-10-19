package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetReference;

public class DefaultPathResolver implements PathResolver {

    @Override
    public String resolvePath(AssetReference assetReference) {
        String pathId = assetReference.getId().substring(assetReference.getId().length() - 3);
        return String.format("%s/%s/%s", pathId.charAt(0), pathId.charAt(1), pathId.charAt(2)).toLowerCase();
    }

}
