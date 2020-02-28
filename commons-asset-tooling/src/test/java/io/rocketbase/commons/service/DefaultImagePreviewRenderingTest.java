package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.dto.asset.Resolution;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
public class DefaultImagePreviewRenderingTest {


    @Test
    public void getPreview() throws Exception {
        // given
        ImagePreviewRendering previewService = new DefaultImagePreviewRendering(getPreviewQualityMap(), null, 0.05f);
        InputStream asset = ClassLoader.class.getResourceAsStream("/assets/max-duzij-qAjJk-un3BI-unsplash.jpg");
        PreviewSize size = PreviewSize.S;

        // when
        ByteArrayOutputStream preview = previewService.getPreview(AssetType.JPEG, asset, size);
        File tempFile = outputToFile(preview);

        // then
        assertThat(preview, notNullValue());
        BufferedImage bufferedImage = ImageIO.read(tempFile);
        assertThat(bufferedImage.getWidth(), lessThanOrEqualTo(size.getMaxWidth()));
        assertThat(bufferedImage.getHeight(), lessThanOrEqualTo(size.getMaxHeight()));
        log.info("see preview-s: {}", tempFile.getAbsolutePath());
    }

    @Test
    public void getLqipJpeg() {
        // given
        ImagePreviewRendering previewService = new DefaultImagePreviewRendering(null, new Resolution(75, 75), 0.07f);
        InputStream asset = ClassLoader.class.getResourceAsStream("/assets/max-duzij-qAjJk-un3BI-unsplash.jpg");

        // when
        String result = previewService.getLqip(AssetType.JPEG, asset);

        // then
        assertThat(result, notNullValue());
        log.info("see lqip: {}", result);
    }

    @Test
    public void getLqipPng() {
        // given
        ImagePreviewRendering previewService = new DefaultImagePreviewRendering(null, new Resolution(50, 50), 0.07f);
        InputStream asset = ClassLoader.class.getResourceAsStream("/assets/pnggrad8rgb.png");

        // when
        String result = previewService.getLqip(AssetType.PNG, asset);

        // then
        assertThat(result, notNullValue());
        log.info("see lqip: {}", result);
    }

    @Test
    public void getLqipGif() {
        // given
        ImagePreviewRendering previewService = new DefaultImagePreviewRendering(null, new Resolution(50, 50), 0.04f);
        InputStream asset = ClassLoader.class.getResourceAsStream("/assets/rocketbase.gif");

        // when
        String result = previewService.getLqip(AssetType.GIF, asset);

        // then
        assertThat(result, notNullValue());
        log.info("see lqip: {}", result);
    }

    protected Map<PreviewSize, Float> getPreviewQualityMap() {
        Map<PreviewSize, Float> result = new HashMap<>();
        for (PreviewSize size : PreviewSize.values()) {
            result.putIfAbsent(size, size.getDefaultQuality());
        }
        return result;
    }

    @SneakyThrows
    protected File outputToFile(ByteArrayOutputStream preview) throws IOException {
        File tempFile = File.createTempFile("asset-preview-s", ".jpg");
        IOUtils.copy(new ByteArrayInputStream(preview.toByteArray()), new FileOutputStream(tempFile));
        return tempFile;
    }
}