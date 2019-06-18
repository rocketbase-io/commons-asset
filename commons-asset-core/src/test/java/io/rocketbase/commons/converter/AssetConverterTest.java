package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.ApiProperties;
import io.rocketbase.commons.config.ThumborProperties;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.dto.asset.Resolution;
import io.rocketbase.commons.model.AssetEntity;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AssetConverterTest {

    @Test
    public void testFromEntityWithLocalRender() {
        // given
        ApiProperties apiProperties = new ApiProperties();
        apiProperties.setBaseUrl("http://localhost:8080");

        AssetConverter converter = new AssetConverter(apiProperties, new DefaultAssetPreviewService(new ThumborProperties(), apiProperties, true));
        // when
        AssetRead assetRead = converter.fromEntity(AssetEntity.builder()
                .id("1235678")
                .urlPath("12345678")
                .fileSize(1234L)
                .created(LocalDateTime.now())
                .originalFilename("original.png")
                .type(AssetType.PNG)
                .systemRefId("123")
                .resolution(new Resolution(100, 200))
                .build(), Arrays.asList(PreviewSize.S, PreviewSize.M, PreviewSize.L));

        // then
        assertThat(assetRead, notNullValue());
        assertThat(assetRead.getPreviews(), notNullValue());
        assertThat(assetRead.getPreviews().getPreviewMap().size(), equalTo(3));
        String baseWithApi = apiProperties.getBaseUrl() + apiProperties.getPath() + "/";
        assertThat(assetRead.getPreviews().getPreviewMap().get(PreviewSize.S), equalTo(baseWithApi + assetRead.getId() + "/s"));
        assertThat(assetRead.getPreviews().getPreviewMap().get(PreviewSize.M), equalTo(baseWithApi + assetRead.getId() + "/m"));
        assertThat(assetRead.getPreviews().getPreviewMap().get(PreviewSize.L), equalTo(baseWithApi + assetRead.getId() + "/l"));

    }
}