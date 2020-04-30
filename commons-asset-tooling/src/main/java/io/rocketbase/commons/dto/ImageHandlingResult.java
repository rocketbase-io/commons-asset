package io.rocketbase.commons.dto;

import com.google.common.io.BaseEncoding;
import io.rocketbase.commons.dto.asset.AssetType;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString(of = {"assetType", "base64"})
public class ImageHandlingResult {
    private final byte[] binary;
    private final AssetType assetType;

    public String base64() {
        return "data:" + assetType.getContentType() + ";base64," + BaseEncoding.base64().encode(binary);
    }

    public byte[] binary() {
        return binary;
    }

    public AssetType assetType() {
        return assetType;
    }

}
