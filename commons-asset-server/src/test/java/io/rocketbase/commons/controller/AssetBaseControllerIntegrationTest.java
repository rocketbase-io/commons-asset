package io.rocketbase.commons.controller;

import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.dto.asset.*;
import io.rocketbase.commons.dto.batch.AssetBatchResult;
import io.rocketbase.commons.dto.batch.AssetBatchWrite;
import io.rocketbase.commons.dto.batch.AssetBatchWriteEntry;
import io.rocketbase.commons.exception.AssetErrorCodes;
import io.rocketbase.commons.exception.BadRequestException;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.resource.AssetResource;
import io.rocketbase.commons.service.AssetService;
import lombok.SneakyThrows;
import org.assertj.core.api.Fail;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ActiveProfiles(profiles = "test")
public class AssetBaseControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Resource
    private AssetService assetService;

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
        assertThat(result.getDownload(), startsWith(getBaseUrl()));
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
    public void testUploadJpgWithKeyValues() {
        // given
        AssetResource assetResource = getAssetResource();

        // when
        File uploadFile = resourceLoader.getResource("classpath:assets/sample.jpg")
                .getFile();
        Map<String, String> keyValues = new HashMap<>();
        String _client = "client";
        String _clientValue = "1";
        keyValues.put(_client, _clientValue);
        String _special = "special";
        String _specialValue = "=?=&";
        keyValues.put(_special, _specialValue);
        String _hidden = "_hidden";
        String _hiddenValue = "secret";
        keyValues.put(_hidden, _hiddenValue);
        String context = "context";
        AssetRead result = assetResource.uploadFile(new FileInputStream(uploadFile), uploadFile.getName(), DefaultAssetUploadMeta.builder()
                .context(context)
                .keyValues(keyValues)
                .build());

        // then
        assertThat(result, notNullValue());
        assertThat(result.getContext(), equalTo(context));
        assertThat(result.getKeyValues(), notNullValue());
        assertThat(result.getKeyValues().containsKey(_client), equalTo(true));
        assertThat(result.getKeyValues().get(_client), equalTo(_clientValue));
        assertThat(result.getKeyValues().containsKey(_special), equalTo(true));
        assertThat(result.getKeyValues().get(_special), equalTo(_specialValue));
        assertThat(result.getKeyValues().containsKey(_hidden), equalTo(false));

        // check hidden
        AssetEntity entity = assetService.findById(result.getId()).orElseThrow(NoClassDefFoundError::new);
        assertThat(entity.getKeyValues(), notNullValue());
        assertThat(entity.getKeyValues().containsKey(_hidden), equalTo(true));
        assertThat(entity.getKeyValues().get(_hidden), equalTo(_hiddenValue));
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
        AssetBatchResult result = assetResource.processBatchFileUrls(new AssetBatchWrite()
                .withEntry(new AssetBatchWriteEntry(success))
                .withEntry(new AssetBatchWriteEntry(failure)));
        //
        assertThat(result, notNullValue());
        assertThat(result.getSucceeded().size(), equalTo(1));
        assertThat(result.getSucceeded().get(success).getType(), equalTo(AssetType.PNG));
        assertThat(result.getSucceeded().get(success).getMeta().getReferenceUrl(), equalTo(success));
        assertThat(result.getFailed().size(), equalTo(1));
        // gitlab response with 404 html page that is text/plain detected
        assertThat(result.getFailed().get(failure), equalTo(AssetErrorCodes.INVALID_CONTENT_TYPE));
    }


    @SneakyThrows
    @Test
    public void testUpdateAsset() {
        // given
        AssetResource assetResource = getAssetResource();

        // when
        File uploadFile = resourceLoader.getResource("classpath:assets/sample.jpg").getFile();
        AssetRead result = assetResource.uploadFile(new FileInputStream(uploadFile), uploadFile.getName());
        assertThat(result.getKeyValues(), equalTo(new HashMap<>()));


        Map<String, String> keyValues = new HashMap<>();
        String _client = "client";
        String _clientValue = "1";
        keyValues.put(_client, _clientValue);
        String _special = "special";
        String _specialValue = "=?=&";
        keyValues.put(_special, _specialValue);
        String _hidden = "_hidden";
        String _hiddenValue = "secret";
        keyValues.put(_hidden, _hiddenValue);

        result = assetResource.update(result.getId(), new AssetUpdate(keyValues));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getKeyValues(), notNullValue());
        assertThat(result.getKeyValues().containsKey(_client), equalTo(true));
        assertThat(result.getKeyValues().get(_client), equalTo(_clientValue));
        assertThat(result.getKeyValues().containsKey(_special), equalTo(true));
        assertThat(result.getKeyValues().get(_special), equalTo(_specialValue));
        assertThat(result.getKeyValues().containsKey(_hidden), equalTo(false));

        // check hidden
        AssetEntity entity = assetService.findById(result.getId()).orElseThrow(NoClassDefFoundError::new);
        assertThat(entity.getKeyValues(), notNullValue());
        assertThat(entity.getKeyValues().containsKey(_hidden), equalTo(true));
        assertThat(entity.getKeyValues().get(_hidden), equalTo(_hiddenValue));
    }

    @SneakyThrows
    @Test
    public void testUploadTooBigAsset() {
        // given
        AssetResource assetResource = getAssetResource();

        // when
        File uploadFile = resourceLoader.getResource("classpath:assets/pierre-bamin-X16N5J0uRD4-unsplash.jpg")
                .getFile();
        AssetRead result = assetResource.uploadFile(new FileInputStream(uploadFile), uploadFile.getName());

        // then
        assertThat(result, notNullValue());
        assertThat(result.getType(), equalTo(AssetType.JPEG));
        assertThat(result.getMeta().getFileSize(), lessThan(uploadFile.length()));
        assertThat(result.getMeta().getResolution(), equalTo(new Resolution(3000, 2000)));
        assertThat(result.getMeta().getOriginalFilename(), equalTo(uploadFile.getName()));
    }

    @SneakyThrows
    @Test
    public void testUploadWebpJpeg() {
        // given
        AssetResource assetResource = getAssetResource();

        // when
        File uploadFile = resourceLoader.getResource("classpath:assets/jpeg.webp")
                .getFile();
        AssetRead result = assetResource.uploadFile(new FileInputStream(uploadFile), uploadFile.getName());

        // then
        assertThat(result, notNullValue());
        assertThat(result.getType(), equalTo(AssetType.WEBP));
        assertThat(result.getMeta().getFileSize(), equalTo(20772L));
        assertThat(result.getMeta().getResolution(), nullValue());
        assertThat(result.getMeta().getOriginalFilename(), equalTo(uploadFile.getName()));
    }

    @SneakyThrows
    @Test
    public void testUploadHeic() {
        // given
        AssetResource assetResource = getAssetResource();

        // when
        File uploadFile = resourceLoader.getResource("classpath:assets/sample.heic")
                .getFile();
        AssetRead result = assetResource.uploadFile(new FileInputStream(uploadFile), uploadFile.getName());

        // then
        assertThat(result, notNullValue());
        assertThat(result.getType(), equalTo(AssetType.HEIC));
        assertThat(result.getMeta().getFileSize(), equalTo(718114L));
        assertThat(result.getMeta().getResolution(), nullValue());
        assertThat(result.getMeta().getOriginalFilename(), equalTo(uploadFile.getName()));
    }

    @SneakyThrows
    @Test
    public void testUploadWebpPng() {
        // given
        AssetResource assetResource = getAssetResource();

        // when
        File uploadFile = resourceLoader.getResource("classpath:assets/png.webp")
                .getFile();
        AssetRead result = assetResource.uploadFile(new FileInputStream(uploadFile), uploadFile.getName());

        // then
        assertThat(result, notNullValue());
        assertThat(result.getType(), equalTo(AssetType.WEBP));
        assertThat(result.getMeta().getFileSize(), equalTo(16674L));
        assertThat(result.getMeta().getResolution(), nullValue());
        assertThat(result.getMeta().getOriginalFilename(), equalTo(uploadFile.getName()));
    }


    private AssetResource getAssetResource() {
        return new AssetResource(getBaseUrl());
    }

}