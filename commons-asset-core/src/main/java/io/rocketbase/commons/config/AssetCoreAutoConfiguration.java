package io.rocketbase.commons.config;

import io.rocketbase.commons.controller.*;
import io.rocketbase.commons.controller.exceptionhandler.*;
import io.rocketbase.commons.converter.*;
import io.rocketbase.commons.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import javax.annotation.Resource;
import java.io.Serializable;

@Configuration
@EnableConfigurationProperties({ApiProperties.class, ThumborProperties.class})
@RequiredArgsConstructor
public class AssetCoreAutoConfiguration implements Serializable {

    private final ApiProperties apiProperties;
    private final ThumborProperties thumborProperties;

    @Resource
    private GridFsTemplate gridFsTemplate;

    @Bean
    @ConditionalOnMissingBean
    public FileStorageService fileStorageService() {
        return new MongoFileStorageService(gridFsTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetTypeFilterService assetTypeFilterService() {
        return new SimpleAssetTypeFilterService(apiProperties.getTypes());
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetPreviewService assetPreviewService(@Autowired FileStorageService fileStorageService) {
        return new DefaultAssetPreviewService(thumborProperties, apiProperties, fileStorageService instanceof MongoFileStorageService);
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetConverter assetConverter(@Autowired AssetPreviewService assetPreviewService) {
        return new AssetConverter(assetPreviewService);
    }

    @Bean
    public AssetService assetService() {
        return new AssetService();
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetIdLoader assetIdLoader() {
        return new DefaultAssetIdLoader();
    }

    @Bean
    public AssetBatchService assetBatchService() {
        return new AssetBatchService();
    }

    @Bean
    public AssetRepository assetRepository(@Autowired MongoTemplate mongoTemplate) {
        return new AssetRepository(mongoTemplate);
    }

    @Bean
    public DownloadService downloadService() {
        return new DownloadService();
    }

    // -------------------------------------------------------
    // --------------------- Controller ----------------------
    // -------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    public AssetBaseController assetBaseController() {
        return new AssetBaseController();
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetBatchController assetBatchController() {
        return new AssetBatchController();
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetDeleteController assetDeleteController() {
        return new AssetDeleteController();
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetDownloadController assetDownloadController() {
        return new AssetDownloadController();
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetPreviewController assetPreviewController() {
        return new AssetPreviewController();
    }

    // -------------------------------------------------------
    // ------------------ ExceptionHandlers ------------------
    // -------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean
    public EmptyFileExceptionHandler emptyFileExceptionHandler() {
        return new EmptyFileExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public InvalidContentTypeExceptionHandler invalidContentTypeExceptionHandler() {
        return new InvalidContentTypeExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public NotAllowedAssetTypeExceptionHandler notAllowedAssetTypeExceptionHandler() {
        return new NotAllowedAssetTypeExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public SystemRefIdAlreadyUsedExceptionHandler systemRefIdAlreadyUsedExceptionHandler() {
        return new SystemRefIdAlreadyUsedExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public UnprocessableAssetExceptionHandler unprocessableAssetExceptionHandler() {
        return new UnprocessableAssetExceptionHandler();
    }

}
