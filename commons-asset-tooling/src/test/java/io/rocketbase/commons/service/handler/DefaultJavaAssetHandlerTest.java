package io.rocketbase.commons.service.handler;

import io.rocketbase.commons.dto.ImageHandlingResult;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.DefaultPreviewParameter;
import io.rocketbase.commons.dto.asset.PreviewSize;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
public class DefaultJavaAssetHandlerTest {

    @Test
    public void getPreview() throws Exception {
        // given
        AssetHandler previewService = new DefaultJavaAssetHandler(AssetHandlerConfig.builder()
                .previewQuality(getPreviewQualityMap())
                .build());

        URL asset = ClassLoader.getSystemResource("assets/max-duzij-qAjJk-un3BI-unsplash.jpg");
        PreviewSize size = PreviewSize.S;

        // when
        File preview = previewService.getPreview(AssetType.JPEG, new File(asset.toURI()), PreviewConfig.builder()
                .previewSize(size)
                .build());

        // then
        assertThat(preview, notNullValue());
        BufferedImage bufferedImage = ImageIO.read(preview);
        assertThat(bufferedImage.getWidth(), lessThanOrEqualTo(size.getMaxWidth()));
        assertThat(bufferedImage.getHeight(), lessThanOrEqualTo(size.getMaxHeight()));
        log.info("see preview-s: {}", preview.getAbsolutePath());
    }

    @Test
    public void getPreviewOfPngWithoutBackground() throws Exception {
        // given
        AssetHandler previewService = new DefaultJavaAssetHandler(AssetHandlerConfig.builder()
                .lqipPreview(new DefaultPreviewParameter(50, 50, 0.07f))
                .build());
        URL asset = ClassLoader.getSystemResource("assets/icon-tomate.png");
        PreviewSize size = PreviewSize.S;

        // when
        File preview = previewService.getPreview(AssetType.PNG, new File(asset.toURI()), PreviewConfig.builder().previewSize(size).build());

        // then
        assertThat(preview, notNullValue());
        BufferedImage bufferedImage = ImageIO.read(preview);
        assertThat(bufferedImage.getWidth(), lessThanOrEqualTo(size.getMaxWidth()));
        assertThat(bufferedImage.getHeight(), lessThanOrEqualTo(size.getMaxHeight()));
        log.info("see preview-s: {}", preview.getAbsolutePath());
    }

    @Test
    public void getPreviewOfPngWithBackground() throws Exception {
        // given
        AssetHandler previewService = new DefaultJavaAssetHandler(AssetHandlerConfig.builder()
                .lqipPreview(new DefaultPreviewParameter(50, 50, 0.07f))
                .build());
        URL asset = ClassLoader.getSystemResource("assets/icon-tomate.png");
        PreviewSize size = PreviewSize.S;

        // when
        File preview = previewService.getPreview(AssetType.PNG, new File(asset.toURI()), PreviewConfig.builder().previewSize(size).bg("#000").rotation(90).build());

        // then
        assertThat(preview, notNullValue());
        BufferedImage bufferedImage = ImageIO.read(preview);
        assertThat(bufferedImage.getWidth(), lessThanOrEqualTo(size.getMaxWidth()));
        assertThat(bufferedImage.getHeight(), lessThanOrEqualTo(size.getMaxHeight()));
        log.info("see preview-s: {}", preview.getAbsolutePath());
    }


    @Test
    public void getLqipJpeg() throws Exception {
        // given
        AssetHandler previewService = new DefaultJavaAssetHandler(AssetHandlerConfig.builder()
                .lqipPreview(new DefaultPreviewParameter(75, 75, 0.07f))
                .build());
        URL asset = ClassLoader.getSystemResource("assets/max-duzij-qAjJk-un3BI-unsplash.jpg");

        // when
        Optional<ImageHandlingResult> result = previewService.getLqip(AssetType.JPEG, new File(asset.toURI()));

        // then
        assertThat(result.isPresent(), equalTo(true));
        log.info("see lqip: {}", result);
    }

    @Test
    public void getLqipPng() throws Exception {
        // given
        AssetHandler previewService = new DefaultJavaAssetHandler(new AssetHandlerConfig());
        URL asset = ClassLoader.getSystemResource("assets/pnggrad8rgb.png");

        // when
        Optional<ImageHandlingResult> result = previewService.getLqip(AssetType.PNG, new File(asset.toURI()));

        // then
        assertThat(result.isPresent(), equalTo(true));
        log.info("see lqip: {}", result);
    }

    @Test
    public void getLqipGif() throws Exception {
        // given
        AssetHandler previewService = new DefaultJavaAssetHandler(AssetHandlerConfig.builder()
                .lqipPreview(new DefaultPreviewParameter(50, 50, 0.07f))
                .build());
        URL asset = ClassLoader.getSystemResource("assets/rocketbase.gif");

        // when
        Optional<ImageHandlingResult> result = previewService.getLqip(AssetType.GIF, new File(asset.toURI()));

        // then
        assertThat(result.isPresent(), equalTo(true));
        log.info("see lqip: {}", result);
    }

    @Test
    public void getLqipThrowErrorDisabled() throws Exception {
        // given
        AssetHandler previewService = new DefaultJavaAssetHandler(AssetHandlerConfig.builder()
                .lqipPreview(new DefaultPreviewParameter(50, 50, 0.07f))
                .build());
        URL asset = ClassLoader.getSystemResource("assets/invalid-sample.txt");

        // when
        Optional<ImageHandlingResult> result = previewService.getLqip(AssetType.TXT, new File(asset.toURI()));

        // then
        assertThat(result.isPresent(), equalTo(false));
    }

    @Test
    public void getLqipThrowErrorEnabled() throws Exception {
        // given
        AssetHandler previewService = new DefaultJavaAssetHandler(AssetHandlerConfig.builder()
                .lqipThrowError(true)
                .lqipPreview(new DefaultPreviewParameter(50, 50, 0.07f))
                .build());
        URL asset = ClassLoader.getSystemResource("assets/invalid-sample.txt");

        // when / then
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            previewService.getLqip(AssetType.TXT, new File(asset.toURI()));
        });
    }

    protected Map<PreviewSize, Float> getPreviewQualityMap() {
        Map<PreviewSize, Float> result = new HashMap<>();
        for (PreviewSize size : PreviewSize.values()) {
            result.putIfAbsent(size, size.getDefaultQuality());
        }
        return result;
    }
}