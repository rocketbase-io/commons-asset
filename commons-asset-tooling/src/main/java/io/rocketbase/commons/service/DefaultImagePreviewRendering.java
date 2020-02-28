package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.dto.asset.Resolution;
import io.rocketbase.commons.tooling.ImageHandler;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.filters.Canvas;
import net.coobird.thumbnailator.geometry.Positions;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

@RequiredArgsConstructor
public class DefaultImagePreviewRendering implements ImagePreviewRendering {

    final Map<PreviewSize, Float> previewQuality;
    final Resolution lqipSize;
    final float lqipQuality;

    @SneakyThrows
    @Override
    public ByteArrayOutputStream getPreview(AssetType assetType, InputStream inputStream, PreviewSize previewSize) {
        ByteArrayOutputStream thumbOs = new ByteArrayOutputStream();
        Thumbnails.Builder<? extends InputStream> thumbBuilder = Thumbnails.of(inputStream)
                .size(previewSize.getMaxWidth(), previewSize.getMaxHeight())
                .outputQuality(previewQuality.getOrDefault(previewSize, 0.8f));
        if (AssetType.PNG.equals(assetType) || AssetType.GIF.equals(assetType)) {
            thumbBuilder.addFilter(new Canvas(previewSize.getMaxWidth(), previewSize.getMaxHeight(), Positions.CENTER, Color.WHITE))
                    .imageType(ThumbnailParameter.DEFAULT_IMAGE_TYPE);
        }
        thumbBuilder.toOutputStream(thumbOs);
        return thumbOs;
    }

    @SneakyThrows
    @Override
    public String getLqip(AssetType assetType, InputStream inputStream) {
        ByteArrayOutputStream thumbOs = new ByteArrayOutputStream();
        Thumbnails.Builder<? extends InputStream> thumbBuilder = Thumbnails.of(inputStream)
                .size(lqipSize.getWidth(), lqipSize.getHeight())
                .outputQuality(lqipQuality)
                .outputFormat("jpg");
        if (AssetType.PNG.equals(assetType) || AssetType.GIF.equals(assetType)) {
            thumbBuilder.addFilter(new Canvas(lqipSize.getWidth(), lqipSize.getHeight(), Positions.CENTER, Color.WHITE))
                    .imageType(ThumbnailParameter.DEFAULT_IMAGE_TYPE);
        }
        thumbBuilder.toOutputStream(thumbOs);
        return new ImageHandler.ImageHandlingResult(thumbOs.toByteArray(), AssetType.JPEG).base64();
    }

}
