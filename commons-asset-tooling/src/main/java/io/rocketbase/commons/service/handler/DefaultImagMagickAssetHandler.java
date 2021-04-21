package io.rocketbase.commons.service.handler;

import com.github.geko444.im4java.core.ConvertCmd;
import com.github.geko444.im4java.core.IMOperation;
import com.github.geko444.im4java.core.Info;
import io.rocketbase.commons.colors.RgbColor;
import io.rocketbase.commons.dto.ImageHandlingResult;
import io.rocketbase.commons.dto.asset.*;
import io.rocketbase.commons.tooling.ColorDetection;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class DefaultImagMagickAssetHandler implements AssetHandler {

    private static final String BACKGROUND_WHITE = "\"#ffffff\"";
    private static final String FIRST_FRAME = "[0]";
    private static final ConvertCmd convertCmd = new ConvertCmd();

    final AssetHandlerConfig config;

    @Override
    public boolean isPreviewSupported(AssetType assetType) {
        return assetType.isImage();
    }

    @SneakyThrows
    public File getPreview(AssetType type, File file, PreviewConfig previewConfig) {
        PreviewParameter previewSize = Nulls.notNull(previewConfig.getPreviewSize(), PreviewSize.S);
        IMOperation operation = new IMOperation();
        operation.addImage(file.getAbsolutePath() + FIRST_FRAME);
        operation.resize(previewSize.getMaxWidth(), previewSize.getMaxHeight());

        if (previewConfig.getRotation() != null) {
            operation.rotate((double) previewConfig.getRotation());
        }

        if (type.couldHaveTransparency()) {
            if (previewConfig.getBg() != null) {
                RgbColor color = RgbColor.readRgbOrHex(previewConfig.getBg());
                operation.background(color != null ? ("\"" + color.getHexCodeWithLeadingHash() + "\"") : BACKGROUND_WHITE);
            }
        } else {
            // quality settings only works for non transparent images
            operation.quality((double) config.getPreviewQuality().getOrDefault(previewSize, 0.8f) * 100);
        }

        File tempFile = File.createTempFile("asset-preview", type.getFileExtensionForSuffix());
        operation.addImage(tempFile.getAbsolutePath());
        convertCmd.run(operation);

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
            if (config.isDetectResolution()) {
                builder.resolution(getResolution(type, file));
            }
            if (config.isDetectColors()) {
                builder.colorPalette(getColorPalette(type, file));
            }
            if (config.isLqipEnabled()) {
                getLqip(type, file).ifPresent(imageHandlingResult ->
                        builder.lqip(imageHandlingResult.base64()));
            }
        }
        return builder.build();
    }

    @Override
    public Resolution getResolution(AssetType type, File file) {
        if (isPreviewSupported(type)) {
            try {
                Info imageInfo = new Info(file.getAbsolutePath() + FIRST_FRAME, true);
                return new Resolution(imageInfo.getImageWidth(), imageInfo.getImageHeight());
            } catch (Exception e) {
                log.error("could not read resolution from file {}, error: {}", file.getPath(), e.getMessage());
            }
        }
        return null;
    }

    public ColorPalette getColorPalette(AssetType type, File file) {
        if (isPreviewSupported(type)) {
            File tempFile = null;
            try {
                IMOperation operation = new IMOperation();
                operation.addImage(file.getAbsolutePath() + FIRST_FRAME);
                // a proper size
                operation.resize(420, 420, ">");
                if (type.couldHaveTransparency()) {
                    operation.background(BACKGROUND_WHITE);
                }
                tempFile = File.createTempFile("asset-analyse-colordetect", ".jpg");
                operation.addImage(tempFile.getAbsolutePath());
                convertCmd.run(operation);

                BufferedImage bufferedImage = ImageIO.read(tempFile);
                ColorPalette result = ColorDetection.detect(bufferedImage);
                return result;
            } catch (Exception e) {
                log.error("could not read colors from file {}, error: {}", file.getPath(), e.getMessage());
            } finally {
                if (tempFile != null) {
                    try {
                        tempFile.delete();
                    } catch (Exception ee) {
                    }
                }
            }
        }
        return null;
    }

    @SneakyThrows
    public Optional<ImageHandlingResult> getLqip(AssetType type, File file) {
        if (!isPreviewSupported(type)) {
            if (config.isLqipThrowError()) {
                throw new UnsupportedOperationException("type " + type.name() + " is not supported");
            } else {
                log.error("lqip type: {} is not supported", type.name());
                return Optional.empty();
            }
        }
        PreviewParameter previewSize = Nulls.notNull(config.getLqipPreview(), PreviewSize.XS);

        IMOperation operation = new IMOperation();
        operation.addImage(file.getAbsolutePath() + FIRST_FRAME);
        // a proper size
        operation.resize(previewSize.getMaxWidth(), previewSize.getMaxHeight());
        operation.quality((double) previewSize.getDefaultQuality());

        if (type.couldHaveTransparency()) {
            operation.background(BACKGROUND_WHITE);
        }

        File tempFile = null;
        try {
            tempFile = File.createTempFile("asset-lqip", ".jpg");
            operation.addImage(tempFile.getAbsolutePath());

            convertCmd.run(operation);
            return Optional.of(new ImageHandlingResult(IOUtils.toByteArray(tempFile.toURI()), AssetType.JPEG));
        } catch (Exception e) {
            log.error("couldn't process lqip for file: {}, error: {}", file.getPath(), e.getMessage());
            if (config.isLqipThrowError()) {
                throw new UnsupportedOperationException("file is not processable");
            }
            return Optional.empty();
        } finally {
            if (tempFile != null) {
                try {
                    tempFile.delete();
                } catch (Exception ee) {
                }
            }
        }
    }
}
