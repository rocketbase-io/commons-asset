package io.rocketbase.commons.exception;

import io.rocketbase.commons.dto.asset.AssetType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NotAllowedAssetTypeException extends RuntimeException {

    private final AssetType assetType;
}
