package io.rocketbase.commons.service;

import io.rocketbase.commons.config.AssetS3Properties;
import io.rocketbase.commons.dto.asset.*;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.util.UrlParts;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
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

@Slf4j
public class S3FileStoreServiceTest {
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
    @Ignore
    public void uploadTest() throws Exception {
        // given
        AssetEntity assetEntity = getSampleAssetEntity();
        URL asset = ClassLoader.getSystemResource("assets/rocketbase.gif");

        // when
        getStorage().upload(assetEntity, new File(asset.toURI()));

        // then
        File file = new File(UrlParts.ensureEndsWithSlash(getBasePath()) + "c/a/e/ac47975c-8fe0-40ad-b811-eb80c899fcae.gif");
        assertThat(file.exists(), equalTo(true));
    }

    @Test
    @Ignore
    public void uploadTestUmlaute() throws Exception {
        // given
        AssetEntity assetEntity = getSampleAssetEntity();
        assetEntity.setReferenceUrl("https://images.unsplash.com/photo-1593642531955-b62e17bdaa9c?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=3750&q=80");
        assetEntity.setOriginalFilename("äö¿ßtest.png");
        URL asset = ClassLoader.getSystemResource("assets/äö¿ßtest.png");

        // when
        getStorage().upload(assetEntity, new File(asset.toURI()));

        // then
    }

    @Test
    public void normalizeOriginalFilename() {
        // given
        String input = "äö¿ßtest.png";
        // when
        String result = getStorage().cleanString(input);

        // then
        assertThat(result, equalTo("aotest.png"));
    }

    @Test
    @Ignore
    public void getDownloadUrl() {
        String url = getStorage().getDownloadUrl(DefaultAssetReference.builder()
                .id("ac47975c-8fe0-40ad-b811-eb80c899fcae")
                .type(AssetType.GIF)
                .context("test")
                .urlPath("c/a/e/ac47975c-8fe0-40ad-b811-eb80c899fcae.gif")
                .build());

        assertThat(url, notNullValue());
    }

    @Test
    @Ignore
    public void downloadTest() throws Exception {
        // given
        AssetEntity assetEntity = getSampleAssetEntity();

        // when
        InputStreamResource download = getStorage().download(assetEntity);
        // then
        assertThat(download, notNullValue());
        File tempFile = File.createTempFile("download-test", ".gif");
        IOUtils.copy(download.getInputStream(), new FileOutputStream(tempFile));
        log.info("downloaded image");
    }

    // @Test
    public void deleteTest() throws Exception {
        // given
        AssetEntity assetEntity = getSampleAssetEntity();

        // when
        getStorage().delete(assetEntity);
        // then
        log.info("is deleted");
    }

    protected AssetEntity getSampleAssetEntity() {
        return SimpleAssetEntity.builder()
                .id("ac47975c-8fe0-40ad-b811-eb80c899fdev")
                .type(AssetType.GIF)
                .context("test")
                .created(Instant.ofEpochMilli(1588529675916L))
                .urlPath("d/e/v/ac47975c-8fe0-40ad-b811-eb80c899fdev.gif")
                .build();
    }

    @SneakyThrows
    protected S3FileStoreService getStorage() {
        AssetS3Properties properties = AssetS3Properties.builder()
                .accessKey("-")
                .secretKey("-")
                .endpoint("https://test.rocketbase.io")
                .pathStyleAccessEnabled(true)
                .signerOverride("AWSS3V4SignerType")
                .region("eu-central-1")
                .downloadExpire(0)
                .publicReadObject(true)
                .build();
        DefaultS3ClientProvider s3ClientProvider = new DefaultS3ClientProvider(properties);
        return new S3FileStoreService(properties, new DefaultBucketResolver("test"), new DefaultPathResolver(), s3ClientProvider.getClient());
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