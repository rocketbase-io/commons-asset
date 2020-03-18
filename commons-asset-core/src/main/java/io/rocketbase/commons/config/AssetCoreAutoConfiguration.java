package io.rocketbase.commons.config;

import io.rocketbase.commons.converter.*;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.dto.asset.Resolution;
import io.rocketbase.commons.service.*;
import io.rocketbase.commons.service.preview.DefaultImagePreviewRendering;
import io.rocketbase.commons.service.preview.ImagePreviewRendering;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({AssetApiProperties.class, AssetLqipProperties.class})
@RequiredArgsConstructor
public class AssetCoreAutoConfiguration implements Serializable {

    private final AssetApiProperties assetApiProperties;
    private final AssetLqipProperties assetLqipProperties;

    @Bean
    @ConditionalOnMissingBean
    public AssetTypeFilterService assetTypeFilterService() {
        return new DefaultAssetTypeFilterService(assetApiProperties.getTypes());
    }

    @Bean
    @ConditionalOnMissingBean(value = AssetPreviewService.class)
    @ConditionalOnNotWebApplication
    public AssetPreviewService assetPreviewService() {
        return new DefaultAssetPreviewService(assetApiProperties);
    }

    @Bean
    @ConditionalOnMissingBean(value = AssetPreviewService.class)
    @ConditionalOnWebApplication
    public AssetPreviewService webAssetPreviewService() {
        return new ServletAssetPreviewService(assetApiProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "asset.converter.enabled", matchIfMissing = true)
    public AssetConverter assetConverter(@Autowired AssetPreviewService assetPreviewService) {
        return new AssetConverter(assetApiProperties, assetPreviewService);
    }

    @Bean
    @ConditionalOnBean(value = AssetRepository.class)
    public AssetService assetService() {
        return new AssetService(assetApiProperties, assetLqipProperties);
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
        return new DefaultDownloadService(assetApiProperties.getDownloadHeaders());
    }

    @Bean
    @ConditionalOnBean(value = AssetService.class)
    @ConditionalOnMissingBean
    public ImagePreviewRendering imagePreviewRendering() {
        Resolution lqipSize = new Resolution(assetLqipProperties.getMaxWidth(), assetLqipProperties.getMaxHeight());
        return new DefaultImagePreviewRendering(getPreviewQualityMap(), lqipSize, assetLqipProperties.getQuality());
    }

    protected Map<PreviewSize, Float> getPreviewQualityMap() {
        Map<String, Float> configMap = Nulls.notNull(assetApiProperties.getPreviewQuality());
        Map<PreviewSize, Float> result = new HashMap<>();
        for (PreviewSize size : PreviewSize.values()) {
            result.put(size, configMap.getOrDefault(size.name(), size.getDefaultQuality()));
        }
        return result;
    }

}
