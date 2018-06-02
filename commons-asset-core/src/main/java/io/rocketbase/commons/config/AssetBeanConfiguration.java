package io.rocketbase.commons.config;

import io.rocketbase.commons.converter.AssetConverter;
import io.rocketbase.commons.service.AssetTypeFilterService;
import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.service.MongoFileStorageService;
import io.rocketbase.commons.service.SimpleAssetTypeFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties({ApiProperties.class, ThumborProperties.class})
@RequiredArgsConstructor
public class AssetBeanConfiguration {

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
    public AssetConverter assetConverter(@Autowired FileStorageService fileStorageService) {
        return new AssetConverter(thumborProperties, apiProperties, fileStorageService);
    }
}