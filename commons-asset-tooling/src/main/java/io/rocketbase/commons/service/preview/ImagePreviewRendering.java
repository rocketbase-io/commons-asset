package io.rocketbase.commons.service.preview;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.PreviewSize;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public interface ImagePreviewRendering {

    /**
     * generates a preview of given inputstream that fits in the given size
     */
    ByteArrayOutputStream getPreview(AssetType assetType, InputStream inputStream, PreviewConfig previewConfig);

    /**
     * an ultra lowres image preview in base64 encoding
     */
    String getLqip(AssetType assetType, InputStream inputStream);
}
