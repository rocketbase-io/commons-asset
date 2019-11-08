package io.rocketbase.commons.config;

import io.rocketbase.commons.converter.*;
import io.rocketbase.commons.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Configuration
@EnableConfigurationProperties({ApiProperties.class})
@RequiredArgsConstructor
public class AssetCoreAutoConfiguration implements Serializable {

    private final ApiProperties apiProperties;

    @Bean
    @ConditionalOnMissingBean
    public AssetTypeFilterService assetTypeFilterService() {
        return new DefaultAssetTypeFilterService(apiProperties.getTypes());
    }

    @Bean
    @ConditionalOnMissingBean(value = AssetPreviewService.class)
    @ConditionalOnNotWebApplication
    public AssetPreviewService assetPreviewService() {
        return new DefaultAssetPreviewService(apiProperties);
    }

    @Bean
    @ConditionalOnMissingBean(value = AssetPreviewService.class)
    @ConditionalOnWebApplication
    public AssetPreviewService webAssetPreviewService() {
        return new ServletAssetPreviewService(apiProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "asset.converter.enabled", matchIfMissing = true)
    public AssetConverter assetConverter(@Autowired AssetPreviewService assetPreviewService) {
        return new AssetConverter(apiProperties, assetPreviewService);
    }

    @Bean
    @ConditionalOnBean(value = AssetRepository.class)
    public AssetService assetService() {
        return new AssetService(apiProperties);
    }

    @Bean
    @ConditionalOnBean(value = AssetService.class)
    @ConditionalOnMissingBean
    public AssetIdLoader assetIdLoader() {
        return new DefaultAssetIdLoader();
    }

    @Bean
    @ConditionalOnBean(value = AssetService.class)
    public AssetBatchService assetBatchService() {
        return new AssetBatchService();
    }

    @Bean
    @ConditionalOnBean(value = AssetService.class)
    @ConditionalOnMissingBean
    public DownloadService downloadService() {
        return new DefaultDownloadService(apiProperties.getDownloadHeaders());
    }

}
