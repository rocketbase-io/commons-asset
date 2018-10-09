package io.rocketbase.commons.config;

import io.rocketbase.commons.converter.*;
import io.rocketbase.commons.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import javax.annotation.Resource;
import java.io.Serializable;

@Configuration
@EnableConfigurationProperties({ApiProperties.class, ThumborProperties.class})
@RequiredArgsConstructor
public class AssetBeanConfiguration implements Serializable {

    private final ApiProperties apiProperties;
    private final ThumborProperties thumborProperties;

    @Resource
    private GridFsTemplate gridFsTemplate;

    @Resource
    private AssetService assetService;

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
    public AssetConverter assetConverter(@Autowired AssetPreviewService assetPreviewService) {
        return new AssetConverter(assetPreviewService);
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetIdLoader assetIdLoader(@Autowired AssetConverter assetConverter) {
        return new DefaultAssetIdLoader(assetService, assetConverter);
    }
}
