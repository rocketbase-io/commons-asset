package io.rocketbase.commons.converter;

import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.dto.asset.AssetId;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.model.AssetMongoEntity;
import io.rocketbase.commons.service.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.annotation.Resource;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.BDDMockito.given;

public class AssetIdLoaderTest extends BaseIntegrationTest {

    @MockBean
    private AssetService assetService;

    @Resource
    private AssetIdLoader assetIdLoader;

    @BeforeEach
    public void before() {
        given(assetService.findById(Mockito.anyString())).willReturn(Optional.of(AssetMongoEntity.builder()
                .id("id123")
                .urlPath("/")
                .originalFilename("sample.jpg")
                .type(AssetType.JPEG)
                .fileSize(10)
                .build()));
    }

    @Test
    public void toRead() {
        // given
        AssetId assetId = new AssetId("id123");

        // when
        AssetRead assetRead = assetIdLoader.toRead(assetId);

        // then
        assertThat(assetRead, notNullValue());
        assertThat(assetRead.getType(), equalTo(AssetType.JPEG));
        assertThat(assetRead.getPreviews(), notNullValue());
    }
}