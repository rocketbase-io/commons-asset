package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.ColorPalette;
import io.rocketbase.commons.dto.asset.Resolution;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.util.UrlParts;
import lombok.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.core.io.InputStreamResource;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class FsFileStorageServiceTest {

    private String basePath;

    @SneakyThrows
    protected String getBasePath() {
        if (basePath == null) {
            File tempFile = File.createTempFile("test", ".txt");
            basePath = tempFile.getPath();
            tempFile.delete();
        }
        return basePath;
    }

    @Test
    public void uploadTest() throws Exception {
        // given
        AssetEntity assetEntity = getSampleAssetEntity();
        URL asset = ClassLoader.class.getResource("/assets/rocketbase.gif");

        // when
        getStorage().upload(assetEntity, new File(asset.toURI()));

        // then
        File file = new File(UrlParts.ensureEndsWithSlash(getBasePath()) + "c/a/e/ac47975c-8fe0-40ad-b811-eb80c899fcae.gif");
        assertThat(file.exists(), equalTo(true));
    }

    @Test
    public void downloadTest() throws Exception {
        // given
        AssetEntity assetEntity = getSampleAssetEntity();
        URL asset = ClassLoader.class.getResource("/assets/rocketbase.gif");
        FileUtils.copyFile(new File(asset.toURI()), new File(UrlParts.ensureEndsWithSlash(getBasePath()) + "c/a/e/ac47975c-8fe0-40ad-b811-eb80c899fcae.gif"));

        // when
        InputStreamResource download = getStorage().download(assetEntity);
        // then
        assertThat(download, notNullValue());
        File tempFile = File.createTempFile("download-test", ".gif");
        IOUtils.copy(download.getInputStream(), new FileOutputStream(tempFile));
        assertThat(tempFile.length(), equalTo(new File(asset.toURI()).length()));
    }

    @Test
    public void deleteTest() throws Exception {
        // given
        AssetEntity assetEntity = getSampleAssetEntity();
        URL asset = ClassLoader.class.getResource("/assets/rocketbase.gif");
        File destFile = new File(UrlParts.ensureEndsWithSlash(getBasePath()) + "c/a/e/ac47975c-8fe0-40ad-b811-eb80c899fcae.gif");
        FileUtils.copyFile(new File(asset.toURI()), destFile);

        // when
        getStorage().delete(assetEntity);
        // then
        assertThat(destFile.exists(), equalTo(false));
    }

    protected AssetEntity getSampleAssetEntity() {
        return SimpleAssetEntity.builder()
                .id("ac47975c-8fe0-40ad-b811-eb80c899fcae")
                .type(AssetType.GIF)
                .context("test")
                .build();
    }

    @SneakyThrows
    protected FsFileStorageService getStorage() {
        return new FsFileStorageService(getBasePath(), new DefaultPathResolver());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static class SimpleAssetEntity implements AssetEntity {
        private String id;

        private String systemRefId;

        private String urlPath;

        private AssetType type;

        private String context;

        private Instant created;

        private String originalFilename;

        private long fileSize;

        private Resolution resolution;

        private ColorPalette colorPalette;

        private String referenceUrl;

        private String lqip;

        private Instant eol;

        private Map<String, String> keyValueMap = new HashMap<>();

        public Map<String, String> getKeyValues() {
            return keyValueMap;
        }
    }
}