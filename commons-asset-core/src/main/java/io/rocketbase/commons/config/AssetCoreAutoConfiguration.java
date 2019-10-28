package io.rocketbase.commons.config;

import io.rocketbase.commons.converter.*;
import io.rocketbase.commons.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.Serializable;

@Configuration
@EnableConfigurationProperties({ApiProperties.class, ThumborProperties.class})
@RequiredArgsConstructor
public class AssetCoreAutoConfiguration implements Serializable {

    private final ApiProperties apiProperties;
    private final ThumborProperties thumborProperties;

    @Resource
    private ApplicationContext applicationContext;

    private Boolean cacheUsesLocalEndpoint;

    private boolean usesLocalEndpoints() {
        if (cacheUsesLocalEndpoint == null) {
            try {
                FileStorageService bean = applicationContext.getBean(FileStorageService.class);
                cacheUsesLocalEndpoint = bean.localEndpoint();
            } catch (BeansException e) {
                cacheUsesLocalEndpoint = apiProperties.isLocalEndpointFallback();
            }
        }
        return cacheUsesLocalEndpoint;
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetTypeFilterService assetTypeFilterService() {
        return new DefaultAssetTypeFilterService(apiProperties.getTypes());
    }

    @Bean
    @ConditionalOnMissingBean(value = AssetPreviewService.class)
    @ConditionalOnNotWebApplication
    public AssetPreviewService assetPreviewService() {
        return new DefaultAssetPreviewService(thumborProperties, apiProperties, usesLocalEndpoints());
    }

    @Bean
    @ConditionalOnMissingBean(value = AssetPreviewService.class)
    @ConditionalOnWebApplication
    public AssetPreviewService webAssetPreviewService() {
        return new ServletAssetPreviewService(thumborProperties, apiProperties, usesLocalEndpoints());
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
