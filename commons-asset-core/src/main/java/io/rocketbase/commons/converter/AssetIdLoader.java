package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.asset.AssetId;
import io.rocketbase.commons.dto.asset.AssetRead;

public interface AssetIdLoader {

    AssetRead toRead(AssetId assetId);

}
