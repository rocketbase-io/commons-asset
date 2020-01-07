package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.asset.AssetId;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.dto.asset.AssetReference;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.AssetService;

import javax.annotation.Resource;
import java.util.Optional;

public class DefaultAssetIdLoader implements AssetIdLoader {

    @Resource
    private AssetService assetService;

    @Resource
    private AssetConverter assetConverter;

    @Override
    public AssetRead toRead(AssetId assetId) {
        AssetEntity entity = assetService.findById(assetId.getValue()).orElse(null);
        if (entity != null) {
            return assetConverter.fromEntity(entity, null);
        }
        return null;
    }

    protected AssetReference toReference(AssetId assetId) {
        if (assetId != null && assetId.getValue() != null) {
            Optional<AssetEntity> entity = assetService.findById(assetId.getValue());
            if (entity.isPresent()) {
                return entity.get().toReference();
            }
        }
        return null;
    }
}
