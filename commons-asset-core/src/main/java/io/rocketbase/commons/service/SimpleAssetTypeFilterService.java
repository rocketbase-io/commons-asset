package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SimpleAssetTypeFilterService implements AssetTypeFilterService {

    @Getter
    private final List<AssetType> allowedAssetTypes;

    @Override
    public boolean isAllowed(AssetType type, AssetUploadDetail detail) {
        return allowedAssetTypes.contains(type);
    }
}
