package io.rocketbase.commons.service.handler;

import io.rocketbase.commons.dto.ImageHandlingResult;
import io.rocketbase.commons.dto.asset.AssetAnalyse;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.Resolution;

import javax.transaction.NotSupportedException;
import java.io.File;

public interface AssetHandler {

    /**
     * detect if preview is possible for given assetType
     */
    boolean isPreviewSupported(AssetType type);

    File getPreview(AssetType type, File file, PreviewConfig previewConfig);

    AssetAnalyse getAnalyse(AssetType type, File file, String originalFilename);

    Resolution getResolution(AssetType type, File file);

    /**
     * an ultra lowres image preview in base64 encoding
     */
    ImageHandlingResult getLqip(AssetType type, File file);
}
