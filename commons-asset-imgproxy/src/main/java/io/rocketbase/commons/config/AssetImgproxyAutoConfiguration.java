package io.rocketbase.commons.config;

import io.rocketbase.commons.converter.AssetPreviewService;
import io.rocketbase.commons.service.BucketResolver;
import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.service.ImgproxyS3AssetPreviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Configuration
@AutoConfigureBefore(AssetCoreAutoConfiguration.class)
@EnableConfigurationProperties({AssetImgproxyProperties.class, AssetS3Properties.class})
@RequiredArgsConstructor
public class AssetImgproxyAutoConfiguration implements Serializable {

    private final AssetApiProperties assetApiProperties;
    private final AssetImgproxyProperties assetImgproxyProperties;
    private final AssetS3Properties assetS3Properties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "asset.imgproxy.s3", matchIfMissing = true)
    public AssetPreviewService s3ImgproxyAssetPreviewService(@Autowired(required = false) BucketResolver bucketResolver,
                                                             @Autowired(required = false) FileStorageService fileStorageService) {
        return new ImgproxyS3AssetPreviewService(assetApiProperties, assetImgproxyProperties, assetS3Properties, bucketResolver, fileStorageService);
    }

}
