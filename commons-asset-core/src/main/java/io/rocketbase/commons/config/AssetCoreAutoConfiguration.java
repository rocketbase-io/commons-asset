package io.rocketbase.commons.config;

import io.rocketbase.commons.converter.*;
import io.rocketbase.commons.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
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
    @ConditionalOnMissingBean(value = AssetPreviewService.class)
    @ConditionalOnNotWebApplication
    public AssetPreviewService assetPreviewService(@Autowired FileStorageService fileStorageService) {
        return new DefaultAssetPreviewService(thumborProperties, apiProperties, fileStorageService instanceof MongoFileStorageService);
    }

    @Bean
    @ConditionalOnMissingBean(value = AssetPreviewService.class)
    @ConditionalOnWebApplication
    public AssetPreviewService webAssetPreviewService(@Autowired FileStorageService fileStorageService) {
        return new ServletAssetPreviewService(thumborProperties, apiProperties, fileStorageService instanceof MongoFileStorageService);
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetConverter assetConverter(@Autowired AssetPreviewService assetPreviewService) {
        return new AssetConverter(assetPreviewService);
    }

    @Bean
    public AssetService assetService() {
        return new AssetService(apiProperties);
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
    @ConditionalOnMissingBean
    public DownloadService downloadService() {
        return new DefaultDownloadService(apiProperties.getDownloadHeaders());
    }

}
