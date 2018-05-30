package io.rocketbase.commons.config;

import io.rocketbase.commons.service.AssetTypeFilterService;
import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.service.MongoFileStorageService;
import io.rocketbase.commons.service.SimpleAssetTypeFilterService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import javax.annotation.Resource;

@Configuration
public class AssetBeanConfiguration {

    @Resource
    private AssetConfiguration assetConfiguration;

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
        return new SimpleAssetTypeFilterService(assetConfiguration.getAllowedAssetTypes());
    }
}
