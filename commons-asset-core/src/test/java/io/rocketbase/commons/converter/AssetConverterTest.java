package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.AssetConfiguration;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.dto.asset.Resolution;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.MongoFileStorageService;
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
        AssetConfiguration config = new AssetConfiguration();
        config.setApiEndpoint("/api/asset");
        config.setRenderEndpoint("/get/asset");

        String baseUrl = "http://localhost:8080" + config.getRenderEndpoint() + "/";

        AssetConverter converter = new AssetConverter(config, new MongoFileStorageService(null));
        // when
        AssetRead assetRead = converter.fromEntity(AssetEntity.builder()
                .id("1235678")
                .urlPath("12345678")
                .fileSize(1234L)
                .created(LocalDateTime.now())
                .originalFilename("originial.png")
                .type(AssetType.PNG)
                .systemRefId("123")
                .resolution(new Resolution(100, 200))
                .build(), Arrays.asList(PreviewSize.S, PreviewSize.M, PreviewSize.L), "http://localhost:8080/");

        // then
        assertThat(assetRead, notNullValue());
        assertThat(assetRead.getPreviews(), notNullValue());
        assertThat(assetRead.getPreviews().getPreviewMap().size(), equalTo(3));
        assertThat(assetRead.getPreviews().getPreviewMap().get(PreviewSize.S), equalTo(baseUrl + assetRead.getId() + "/s"));
        assertThat(assetRead.getPreviews().getPreviewMap().get(PreviewSize.M), equalTo(baseUrl + assetRead.getId() + "/m"));
        assertThat(assetRead.getPreviews().getPreviewMap().get(PreviewSize.L), equalTo(baseUrl + assetRead.getId() + "/l"));

    }
}