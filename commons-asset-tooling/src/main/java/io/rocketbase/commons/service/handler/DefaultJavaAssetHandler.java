package io.rocketbase.commons.service.handler;

import com.google.common.collect.Sets;
import io.rocketbase.commons.colors.RgbColor;
import io.rocketbase.commons.dto.ImageHandlingResult;
import io.rocketbase.commons.dto.asset.*;
import io.rocketbase.commons.tooling.ColorDetection;
import io.rocketbase.commons.util.Nulls;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.ThumbnailParameter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.filters.Canvas;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.Instant;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class DefaultJavaAssetHandler implements AssetHandler {

    private final static Set<AssetType> SUPPORTED_TYPES = Sets.newHashSet(AssetType.JPEG, AssetType.PNG, AssetType.GIF, AssetType.TIFF);

    final AssetHandlerConfig config;

    @Override
    public boolean isPreviewSupported(AssetType assetType) {
        return SUPPORTED_TYPES.contains(assetType);
    }

    @SneakyThrows
    public File getPreview(AssetType assetType, File file, PreviewConfig previewConfig) {
        PreviewParameter previewSize = Nulls.notNull(previewConfig.getPreviewSize(), PreviewSize.S);
        Thumbnails.Builder<? extends File> thumbBuilder = Thumbnails.of(file)
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
                    .outputQuality(config.getPreviewQuality().getOrDefault(previewSize, 0.8f));
        }
        File tempFile = File.createTempFile("asset-preview", assetType.getFileExtensionForSuffix());
        thumbBuilder.toFile(tempFile);
        return tempFile;
    }

    @Override
    public AssetAnalyse getAnalyse(AssetType type, File file, String originalFilename) {
        AssetAnalyse.AssetAnalyseBuilder builder = AssetAnalyse.builderAnalyse()
                .type(type)
                .fileSize(file.length())
                .created(Instant.now())
                .originalFilename(originalFilename);

        if (isPreviewSupported(type) && config.isImageProcessingNeeded()) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                if (config.isDetectResolution()) {
                    if (bufferedImage != null) {
                        builder.resolution(new Resolution(bufferedImage.getWidth(), bufferedImage.getHeight()));
                    } else {
                        log.trace("file not readable");
                    }
                }
                if (config.isDetectColors()) {
                    builder.colorPalette(ColorDetection.detect(bufferedImage));
                }
                if (config.isLqipEnabled()) {
                    builder.lqip(getLqip(type, Thumbnails.of(bufferedImage)).base64());
                }
            } catch (Exception e) {
                log.error("could not read file information from file {}", file.getPath());
            }
        }
        return builder.build();
    }

    public ImageHandlingResult getLqip(AssetType assetType, File file) {
        if (!isPreviewSupported(assetType)) {
            throw new UnsupportedOperationException("assetType " + assetType.name() + " is not supported");
        }
        return getLqip(assetType, Thumbnails.of(file));
    }

    @SneakyThrows
    protected ImageHandlingResult getLqip(AssetType assetType, Thumbnails.Builder builder) {
        PreviewParameter previewSize = Nulls.notNull(config.getLqipPreview(), PreviewSize.XS);
        builder
                .size(previewSize.getMaxWidth(), previewSize.getMaxHeight())
                .outputQuality(previewSize.getDefaultQuality())
                .outputFormat("jpg");
        if (AssetType.PNG.equals(assetType) || AssetType.GIF.equals(assetType)) {
            builder.addFilter(new Canvas(previewSize.getMaxWidth(), previewSize.getMaxHeight(), Positions.CENTER, Color.WHITE))
                    .imageType(ThumbnailParameter.DEFAULT_IMAGE_TYPE);
        }

        @Cleanup ByteArrayOutputStream thumbOs = new ByteArrayOutputStream();
        builder.toOutputStream(thumbOs);
        return new ImageHandlingResult(thumbOs.toByteArray(), AssetType.JPEG);
    }
}
