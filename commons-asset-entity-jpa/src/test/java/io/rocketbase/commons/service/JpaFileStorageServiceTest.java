package io.rocketbase.commons.service;

import io.rocketbase.commons.Application;
import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.ColorPalette;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.dto.asset.Resolution;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.model.AssetFileEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
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
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class JpaFileStorageServiceTest {

    @Resource
    private EntityManager entityManager;

    @Test
    public void uploadTest() throws Exception {
        // given
        AssetEntity assetEntity = getSampleAssetEntity();
        URL asset = ClassLoader.getSystemResource("assets/rocketbase.gif");
        File file = new File(asset.toURI());

        // when
        getStorage().upload(assetEntity, file);

        // then
        InputStreamResource download = getStorage().download(assetEntity);
        File downloadFile = File.createTempFile("download", ".gif");
        IOUtils.copy(download.getInputStream(), new FileOutputStream(downloadFile));
        assertThat(downloadFile.length(), equalTo(file.length()));
    }


    @Test(expected = NotFoundException.class)
    public void deleteTest() throws Exception {
        // given
        AssetEntity assetEntity = getSampleAssetEntity();
        URL asset = ClassLoader.getSystemResource("assets/rocketbase.gif");
        File file = new File(asset.toURI());
        getStorage().upload(assetEntity, file);

        // when
        getStorage().delete(assetEntity);
        // then
        getStorage().download(assetEntity);
    }

    @Test
    public void deleteWithPreviewsTest() throws Exception {
        // given
        AssetEntity assetEntity = getSampleAssetEntity();
        URL asset = ClassLoader.getSystemResource("assets/rocketbase.gif");
        File file = new File(asset.toURI());
        getStorage().upload(assetEntity, file);
        getStorage().storePreview(assetEntity, file, PreviewSize.S);

        // when
        getStorage(true).delete(assetEntity);
        // then
        AssetFileEntity source = entityManager.find(AssetFileEntity.class, getStorage().buildPreviewSizeId(assetEntity, PreviewSize.S));
        assertThat(source, notNullValue());
    }

    @Test
    public void copyTest() throws Exception {
        // given
        AssetEntity copySource = getSampleAssetEntity();
        URL asset = ClassLoader.getSystemResource("assets/rocketbase.gif");
        getStorage().upload(copySource, new File(asset.toURI()));

        AssetEntity copyTarget = getSampleAssetEntity();
        copyTarget.setId("ae38ecaa-e131-4132-b1a5-f2f92cf4750d");

        // when
        getStorage().copy(copySource, copyTarget);

        // then
        AssetFileEntity source = entityManager.find(AssetFileEntity.class, copySource.getId());
        assertThat(source, notNullValue());
        AssetFileEntity copied = entityManager.find(AssetFileEntity.class, copyTarget.getId());
        assertThat(copied, notNullValue());
    }

    protected AssetEntity getSampleAssetEntity() {
        return SimpleAssetEntity.builder()
                .id("ac47975c-8fe0-40ad-b811-eb80c899fcae")
                .type(AssetType.GIF)
                .context("test")
                .build();
    }

    @SneakyThrows
    protected JpaFileStorageService getStorage() {
        return getStorage(false);
    }

    @SneakyThrows
    protected JpaFileStorageService getStorage(boolean precalulate) {
        AssetApiProperties assetApiProperties = new AssetApiProperties();
        assetApiProperties.setPrecalculate(precalulate);
        return new JpaFileStorageService(entityManager, assetApiProperties);
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