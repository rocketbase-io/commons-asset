package io.rocketbase.commons.service.preview;

import io.rocketbase.commons.colors.RgbColor;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.dto.asset.Resolution;
import io.rocketbase.commons.tooling.ImageHandler;
import io.rocketbase.commons.util.Nulls;
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
    public ByteArrayOutputStream getPreview(AssetType assetType, InputStream inputStream, PreviewConfig previewConfig) {
        ByteArrayOutputStream thumbOs = new ByteArrayOutputStream();
        PreviewSize previewSize = Nulls.notNull(previewConfig.getPreviewSize(), PreviewSize.S);
        Thumbnails.Builder<? extends InputStream> thumbBuilder = Thumbnails.of(inputStream)
                .size(previewSize.getMaxWidth(), previewSize.getMaxHeight());

        if (previewConfig.getRotation() != null) {
            thumbBuilder.rotate(previewConfig.getRotation());
        }

        if ((AssetType.PNG.equals(assetType) || AssetType.GIF.equals(assetType))) {
            if (previewConfig.getBg() != null) {
                // optional set background color
                RgbColor rgbColor = Nulls.notNull(RgbColor.readRgb(previewConfig.getBg()), RgbColor.hex2rgb(previewConfig.getBg()));
                Color color = rgbColor != null ? new Color(rgbColor.getR(), rgbColor.getG(), rgbColor.getB()) : Color.WHITE;
                thumbBuilder.addFilter(new Canvas(previewSize.getMaxWidth(), previewSize.getMaxHeight(), Positions.CENTER, color))
                        .imageType(ThumbnailParameter.DEFAULT_IMAGE_TYPE);
            }
        } else {
            // quality settings only works for non transparent images
            thumbBuilder
                    .outputQuality(previewQuality.getOrDefault(previewSize, 0.8f));
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
