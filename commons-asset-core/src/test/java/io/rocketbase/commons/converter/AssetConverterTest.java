package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.dto.asset.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AssetConverterTest {

    @Test
    public void toRead() {
        // given
        AssetConverter converter = new AssetConverter(new AssetApiProperties(), new TestAssetPreviewService());
        // when
        DefaultAssetReference reference = DefaultAssetReference.builder()
                .id("1234")
                .type(AssetType.JPEG)
                .meta(AssetMeta.builder()
                        .resolution(new Resolution(1536, 2048))
                        .build())
                .build();

        AssetRead assetRead = converter.toRead(reference, Arrays.asList(PreviewSize.values()));
        // then
        assertThat(assetRead, notNullValue());
        assertThat(assetRead.getPreviews(), notNullValue());
        assertThat(assetRead.getPreviews().getPreviewMap().keySet(), containsInAnyOrder(PreviewSize.values()));
        ResponsiveImage responsive = assetRead.getPreviews().getResponsive();
        assertThat(responsive, notNullValue());
        assertThat(responsive.getSrc(), equalTo(reference.getId() + "-XL.jpg"));
        Resolution maxWidth = reference.getMeta().getResolution().calculateWithAspectRatio(PreviewSize.XL.getMaxWidth(), PreviewSize.XL.getMaxHeight());
        assertThat(responsive.getSizes(), containsString(maxWidth.getWidth() + "px"));
        assertThat(responsive.getSrcset(), containsString(String.format("XL.jpg %dw", maxWidth.getWidth())));
    }

    @Test
    public void toReadWithoutResolution() {
        // given
        AssetConverter converter = new AssetConverter(new AssetApiProperties(), new TestAssetPreviewService());
        // when
        DefaultAssetReference reference = DefaultAssetReference.builder()
                .id("1234")
                .type(AssetType.JPEG)
                .meta(AssetMeta.builder()
                        .build())
                .build();

        AssetRead assetRead = converter.toRead(reference, Arrays.asList(PreviewSize.values()));
        // then
        assertThat(assetRead, notNullValue());
        assertThat(assetRead.getPreviews(), notNullValue());
        assertThat(assetRead.getPreviews().getPreviewMap().keySet(), containsInAnyOrder(PreviewSize.values()));
        ResponsiveImage responsive = assetRead.getPreviews().getResponsive();
        assertThat(responsive, nullValue());
    }

    @Test
    public void toReadSmallOriginal() {
        // given
        AssetConverter converter = new AssetConverter(new AssetApiProperties(), new TestAssetPreviewService());
        // when
        DefaultAssetReference reference = DefaultAssetReference.builder()
                .id("1234")
                .type(AssetType.JPEG)
                .meta(AssetMeta.builder()
                        .resolution(new Resolution(940, 600))
                        .build())
                .build();

        AssetRead assetRead = converter.toRead(reference, Arrays.asList(PreviewSize.values()));
        // then
        assertThat(assetRead, notNullValue());
        assertThat(assetRead.getPreviews(), notNullValue());
        assertThat(assetRead.getPreviews().getPreviewMap().size(), equalTo(4));
        assertThat(assetRead.getPreviews().getPreviewMap().keySet(), containsInAnyOrder(PreviewSize.XS, PreviewSize.S, PreviewSize.M, PreviewSize.L));
        ResponsiveImage responsive = assetRead.getPreviews().getResponsive();
        assertThat(responsive, notNullValue());
        assertThat(responsive.getSrc(), equalTo(reference.getId() + "-download.jpg"));
        assertThat(responsive.getSizes(), containsString(reference.getMeta().getResolution().getWidth() + "px"));
        assertThat(responsive.getSrcset(), containsString(String.format("download.jpg %dw", reference.getMeta().getResolution().getWidth())));
    }

    public static class TestAssetPreviewService implements AssetPreviewService {

        @Override
        public boolean isPreviewSupported(AssetType assetType) {
            return assetType.isImage();
        }

        @Override
        public String getPreviewUrl(AssetReference assetReference, PreviewSize size) {
            return String.format("%s-%s.%s", assetReference.getId(), size, assetReference.getType().getFileExtension());
        }

        @Override
        public String getDownloadUrl(AssetReference assetReference) {
            return String.format("%s-download.%s", assetReference.getId(), assetReference.getType().getFileExtension());
        }
    }
}