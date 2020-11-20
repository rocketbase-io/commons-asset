package io.rocketbase.commons.config;

import io.rocketbase.commons.converter.*;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.dto.asset.DefaultPreviewParameter;
import io.rocketbase.commons.service.*;
import io.rocketbase.commons.service.download.DefaultDownloadService;
import io.rocketbase.commons.service.download.DownloadService;
import io.rocketbase.commons.service.handler.AssetHandler;
import io.rocketbase.commons.service.handler.AssetHandlerConfig;
import io.rocketbase.commons.service.handler.DefaultImagMagickAssetHandler;
import io.rocketbase.commons.service.handler.DefaultJavaAssetHandler;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({AssetApiProperties.class, AssetLqipProperties.class, AssetShrinkProperties.class})
@RequiredArgsConstructor
public class AssetCoreAutoConfiguration implements Serializable {

    private final AssetApiProperties assetApiProperties;
    private final AssetLqipProperties assetLqipProperties;
    private final AssetShrinkProperties assetShrinkProperties;

    @Value("${asset.imagemagick.enabled:false}")
    private Boolean imageMagickEnabled;

    @Bean
    @ConditionalOnMissingBean
    public AssetTypeFilterService assetTypeFilterService() {
        return new DefaultAssetTypeFilterService(assetApiProperties.getTypes());
    }

    @Bean
    @ConditionalOnMissingBean(value = AssetPreviewService.class)
    @ConditionalOnNotWebApplication
    @ConditionalOnProperty(name = "asset.api.precalculate", havingValue = "false", matchIfMissing = true)
    public AssetPreviewService assetPreviewService(@Autowired(required = false) FileStorageService fileStorageService) {
        return new DefaultAssetPreviewService(assetApiProperties, fileStorageService, imageMagickEnabled);
    }

    @Bean
    @ConditionalOnMissingBean(value = AssetPreviewService.class)
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "asset.api.precalculate", havingValue = "false", matchIfMissing = true)
    public AssetPreviewService webAssetPreviewService(@Autowired(required = false) FileStorageService fileStorageService) {
        return new ServletAssetPreviewService(assetApiProperties, fileStorageService, imageMagickEnabled);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "asset.converter.enabled", matchIfMissing = true)
    public AssetConverter assetConverter(@Autowired AssetPreviewService assetPreviewService) {
        return new AssetConverter(assetApiProperties, assetPreviewService);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "asset.shrink.enabled", havingValue = "false", matchIfMissing = true)
    public OriginalUploadModifier defaultOriginalUploadModifier() {
        return new DefaultOriginalUploadModifier();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "asset.shrink.enabled", havingValue = "true")
    public OriginalUploadModifier shrinkOriginalUploadModifier(@Autowired AssetHandler assetHandler) {
        return new DefaultShrinkOriginalUploadModifier(assetShrinkProperties, assetHandler);
    }

    @Bean
    @ConditionalOnBean(value = AssetRepository.class)
    public AssetService assetService() {
        return new AssetService(assetApiProperties);
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
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "asset.imagemagick.enabled", havingValue = "false", matchIfMissing = true)
    public AssetHandler assetHandler() {
        return new DefaultJavaAssetHandler(getAssetHandlerConfig());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "asset.imagemagick.enabled", havingValue = "true")
    public AssetHandler imageMagickAssetHandler() {
        return new DefaultImagMagickAssetHandler(getAssetHandlerConfig());
    }


    @Bean
    @ConditionalOnProperty(name = "asset.api.precalculate", havingValue = "true")
    public AssetPreviewService assetPreviewServicePrecalculated(@Autowired(required = false) FileStorageService fileStorageService) {
        return new PrecalculatedAssetPreviewService(assetApiProperties, imageMagickEnabled, fileStorageService);
    }

    @Bean
    @ConditionalOnProperty(name = "asset.api.precalculate", havingValue = "true")
    public PrecalculateOnAfterUploadEventListener precalculateOnAfterUploadEventListener(@Autowired AssetHandler assetHandler, @Autowired FileStorageService fileStorageService) {
        return new PrecalculateOnAfterUploadEventListener(assetApiProperties, assetHandler, fileStorageService);
    }

    @Bean
    @ConditionalOnMissingBean
    public PathResolver pathResolver() {
        return new DefaultPathResolver();
    }

    protected AssetHandlerConfig getAssetHandlerConfig() {
        return AssetHandlerConfig.builder()
                .previewQuality(getPreviewQualityMap())
                .detectColors(assetApiProperties.isDetectColors())
                .detectResolution(assetApiProperties.isDetectResolution())
                .lqipEnabled(assetLqipProperties.isEnabled())
                .lqipPreview(new DefaultPreviewParameter(assetLqipProperties.getMaxWidth(), assetLqipProperties.getMaxHeight(), assetLqipProperties.getQuality()))
                .build();
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
