package io.rocketbase.commons.controller;

import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.Resolution;
import io.rocketbase.commons.dto.batch.AssetBatchResult;
import io.rocketbase.commons.dto.batch.AssetBatchWrite;
import io.rocketbase.commons.dto.batch.AssetBatchWriteEntry;
import io.rocketbase.commons.exception.AssetErrorCodes;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.resource.AssetResource;
import lombok.SneakyThrows;
import org.assertj.core.api.Fail;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;

import static org.assertj.core.api.Fail.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AssetBaseControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @SneakyThrows
    @Test
    public void testUploadPng() {
        // given
        AssetResource assetResource = getAssetResource();

        // when
        File uploadFile = resourceLoader.getResource("classpath:assets/sample.png")
                .getFile();
        AssetRead result = assetResource.uploadFile(new FileInputStream(uploadFile), uploadFile.getName());

        // then
        assertThat(result, notNullValue());
        assertThat(result.getType(), equalTo(AssetType.PNG));
        assertThat(result.getMeta().getFileSize(), equalTo(3572L));
        assertThat(result.getMeta().getResolution(), equalTo(new Resolution(200, 40)));
        assertThat(result.getMeta().getOriginalFilename(), equalTo(uploadFile.getName()));

    }

    @SneakyThrows
    @Test
    public void testUploadJpg() {
        // given
        AssetResource assetResource = getAssetResource();

        // when
        File uploadFile = resourceLoader.getResource("classpath:assets/sample.jpg")
                .getFile();
        AssetRead result = assetResource.uploadFile(new FileInputStream(uploadFile), uploadFile.getName());

        // then
        assertThat(result, notNullValue());
        assertThat(result.getType(), equalTo(AssetType.JPEG));
        assertThat(result.getMeta().getFileSize(), equalTo(9509L));
        assertThat(result.getMeta().getResolution(), equalTo(new Resolution(200, 40)));
        assertThat(result.getMeta().getOriginalFilename(), equalTo(uploadFile.getName()));
    }

    @SneakyThrows
    @Test
    public void testUploadGif() {
        // given
        AssetResource assetResource = getAssetResource();

        // when
        File uploadFile = resourceLoader.getResource("classpath:assets/sample.gif")
                .getFile();
        AssetRead result = assetResource.uploadFile(new FileInputStream(uploadFile), uploadFile.getName());

        // then
        assertThat(result, notNullValue());
        assertThat(result.getType(), equalTo(AssetType.GIF));
        assertThat(result.getMeta().getFileSize(), equalTo(477L));
        assertThat(result.getMeta().getResolution(), equalTo(new Resolution(200, 40)));
        assertThat(result.getMeta().getOriginalFilename(), equalTo(uploadFile.getName()));
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
        assertThat(result.getMeta().getFileSize(), equalTo(9519L));
        assertThat(result.getMeta().getResolution(), nullValue());
        assertThat(result.getMeta().getOriginalFilename(), equalTo(uploadFile.getName()));
    }

    @SneakyThrows
    @Test
    public void testUploadZip() {
        // given
        AssetResource assetResource = getAssetResource();

        // when
        File uploadFile = resourceLoader.getResource("classpath:assets/sample.zip")
                .getFile();
        AssetRead result = assetResource.uploadFile(new FileInputStream(uploadFile), uploadFile.getName());

        // then
        assertThat(result, notNullValue());
        assertThat(result.getType(), equalTo(AssetType.ZIP));
        assertThat(result.getMeta().getFileSize(), equalTo(631L));
        assertThat(result.getMeta().getResolution(), nullValue());
        assertThat(result.getMeta().getOriginalFilename(), equalTo(uploadFile.getName()));
    }

    @SneakyThrows
    @Test
    public void testDelete() {
        // given
        AssetResource assetResource = getAssetResource();
        File uploadFile = resourceLoader.getResource("classpath:assets/sample.zip")
                .getFile();
        AssetRead result = assetResource.uploadFile(new FileInputStream(uploadFile), uploadFile.getName());

        // when
        assetResource.delete(result.getId());
        result = assetResource.find(result.getId());

        // then
        assertThat(result, nullValue());
    }

    @SneakyThrows
    @Test
    public void testUploadInvalidContentType() {
        // given
        AssetResource assetResource = getAssetResource();

        // when
        File uploadFile = resourceLoader.getResource("classpath:assets/invalid-sample.txt")
                .getFile();
        try {
            // then
            AssetRead result = assetResource.uploadFile(new FileInputStream(uploadFile), uploadFile.getName());
            Fail.fail("txt shouldn't be able to proceed");
        } catch (BadRequestException e) {
            assertThat(e.getErrorResponse().getStatus(), equalTo(AssetErrorCodes.INVALID_CONTENT_TYPE.getStatus()));
        }
    }

    @SneakyThrows
    //@Test
    public void testBatch() {
        //
        AssetResource assetResource = getAssetResource();
        String success = "https://cdn.rocketbase.io/assets/signature/rocketbase-signature-20179.png";
        String failure = "https://gitlab.com/notfound.jpg";

        // when
        AssetBatchResult result = assetResource.processBatchFileUrls(AssetBatchWrite.builder()
                .entry(new AssetBatchWriteEntry(success))
                .entry(new AssetBatchWriteEntry(failure))
                .build());
        //
        assertThat(result, notNullValue());
        assertThat(result.getSucceeded().size(), equalTo(1));
        assertThat(result.getSucceeded().get(success).getType(), equalTo(AssetType.PNG));
        assertThat(result.getSucceeded().get(success).getMeta().getReferenceUrl(), equalTo(success));
        assertThat(result.getFailed().size(), equalTo(1));
        // gitlab response with 404 html page that is text/plain detected
        assertThat(result.getFailed().get(failure), equalTo(AssetErrorCodes.INVALID_CONTENT_TYPE));

    }

    @Nonnull
    private AssetResource getAssetResource() {
        return new AssetResource(getBaseUrl());
    }

}