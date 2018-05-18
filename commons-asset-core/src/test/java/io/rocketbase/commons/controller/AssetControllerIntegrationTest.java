package io.rocketbase.commons.controller;

import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.Resolution;
import io.rocketbase.commons.exception.AssetErrorCodes;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.resource.AssetResource;
import io.rocketbase.commons.resource.BasicResponseErrorHandler;
import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;

import static org.assertj.core.api.Fail.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AssetControllerIntegrationTest extends BaseIntegrationTest {

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
    public void testUploadInvalidContentType() {
        // given
        AssetResource assetResource = getAssetResource();

        // when
        File uploadFile = resourceLoader.getResource("classpath:assets/invalid-sample.txt")
                .getFile();
        try {
            // then
            AssetRead result = assetResource.uploadFile(new FileInputStream(uploadFile), uploadFile.getName());
            fail("txt shouldn't be able to proceed");
        } catch (BadRequestException e) {
            assertThat(e.getErrorResponse().getStatus(), equalTo(AssetErrorCodes.INVALID_CONTENT_TYPE.getStatus()));
        }
    }

    @Nonnull
    private AssetResource getAssetResource() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new BasicResponseErrorHandler());
        return new AssetResource(restTemplate, getBaseUrl());
    }

}