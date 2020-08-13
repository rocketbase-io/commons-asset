package io.rocketbase.commons.controller;

import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.dto.asset.AssetAnalyse;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.resource.AssetResource;
import io.rocketbase.commons.service.AssetService;
import io.rocketbase.commons.service.MongoFileStorageService;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ActiveProfiles(profiles = "precalculate")
public class AssetBaseControllerWithPrecalculatedIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Resource
    private MongoFileStorageService mongoFileStorageService;

    @Resource
    private AssetService assetService;

    @SneakyThrows
    @Test
    public void testUploadJpg() {
        // given
        AssetResource assetResource = getAssetResource();

        // when
        File uploadFile = resourceLoader.getResource("classpath:assets/photo-1595776430819-9aa35f06830b.jpeg")
                .getFile();
        AssetRead result = assetResource.uploadFile(new FileInputStream(uploadFile), uploadFile.getName());

        // then
        assertThat(result, notNullValue());
        assertThat(result.getType(), equalTo(AssetType.JPEG));
        assertThat(result.getPreviews().getPreviewMap().keySet().size(), equalTo(2));
        assertThat(result.getPreviews().getPreviewMap().keySet(), containsInAnyOrder(PreviewSize.S, PreviewSize.M));


        File tempSPreviewFile = File.createTempFile("asset-", ".jpg");
        IOUtils.copy(mongoFileStorageService.downloadPreview(result, PreviewSize.S).getInputStream(), new FileOutputStream(tempSPreviewFile));
        AssetAnalyse analyseS = assetService.analyse(tempSPreviewFile, "test-s.jpg");

        assertThat(analyseS, notNullValue());
        assertThat(analyseS.getResolution().getWidth(), equalTo(PreviewSize.S.getMaxWidth()));
        assertThat(analyseS.getResolution().getHeight(), lessThanOrEqualTo(PreviewSize.S.getMaxHeight()));

        File tempMPreviewFile = File.createTempFile("asset-", ".jpg");
        IOUtils.copy(mongoFileStorageService.downloadPreview(result, PreviewSize.M).getInputStream(), new FileOutputStream(tempMPreviewFile));
        AssetAnalyse analyseM = assetService.analyse(tempMPreviewFile, "test-m.jpg");

        assertThat(analyseM, notNullValue());
        assertThat(analyseM.getResolution().getWidth(), equalTo(PreviewSize.M.getMaxWidth()));
        assertThat(analyseM.getResolution().getHeight(), lessThanOrEqualTo(PreviewSize.M.getMaxHeight()));
    }

    @SneakyThrows
    @Test
    public void testUploadPdf() {
        // given
        AssetResource assetResource = getAssetResource();

        // when
        File uploadFile = resourceLoader.getResource("classpath:assets/sample.pdf")
                .getFile();
        AssetRead result = assetResource.uploadFile(new FileInputStream(uploadFile), uploadFile.getName());

        // then
        assertThat(result, notNullValue());
        assertThat(result.getType(), equalTo(AssetType.PDF));
        assertThat(result.getPreviews(), nullValue());

        try {
            mongoFileStorageService.downloadPreview(result, PreviewSize.M);
            throw new RuntimeException("should throw NotFoundException");
        } catch (Exception e) {
            assertThat(e, instanceOf(NotFoundException.class));
        }
    }


    private AssetResource getAssetResource() {
        return new AssetResource(getBaseUrl());
    }

}