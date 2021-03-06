package io.rocketbase.commons.controller;

import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.dto.asset.AssetAnalyse;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.Resolution;
import io.rocketbase.commons.dto.batch.AssetBatchAnalyseResult;
import io.rocketbase.commons.exception.AssetErrorCodes;
import io.rocketbase.commons.resource.AssetResource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@ActiveProfiles(profiles = "test")
@Slf4j
public class AssetAnalyseControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @SneakyThrows
    @Test
    public void testAnalyseFile() {
        // given
        AssetResource assetResource = new AssetResource(baseUrl);

        // when
        File uploadFile = resourceLoader.getResource("classpath:assets/sample.png")
                .getFile();
        AssetAnalyse result = assetResource.analyseFile(new FileInputStream(uploadFile), "sample.png");

        // then
        assertThat(result, notNullValue());
        assertThat(result.getType(), equalTo(AssetType.PNG));
        assertThat(result.getFileSize(), equalTo(3572L));
        assertThat(result.getResolution(), equalTo(new Resolution(200, 40)));
        assertThat(result.getColorPalette(), notNullValue());
        assertThat(result.getLqip(), notNullValue());
    }

    @SneakyThrows
    @Test
    public void testProcessBatchAnalyse() {
        //
        AssetResource assetResource = new AssetResource(baseUrl);
        String success = "https://cdn.rocketbase.io/assets/signature/rocketbase-signature-20179.png";
        String failure = "https://gitlab.com/notfound.jpg";

        // when
        AssetBatchAnalyseResult result = assetResource.processBatchAnalyseUrls(Arrays.asList(success, failure));

        //
        assertThat(result, notNullValue());
        assertThat(result.getSucceeded().size(), equalTo(1));
        assertThat(result.getSucceeded().get(success).getType(), equalTo(AssetType.PNG));
        assertThat(result.getSucceeded().get(success).getColorPalette(), notNullValue());
        assertThat(result.getFailed().size(), equalTo(1));
        // gitlab response with 404 html page that is text/plain detected
        assertThat(result.getFailed().get(failure), equalTo(AssetErrorCodes.NOT_DOWNLOADABLE));

    }

}