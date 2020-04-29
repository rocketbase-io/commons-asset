package io.rocketbase.commons.config;

import io.rocketbase.commons.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties({AssetS3Properties.class})
@AutoConfigureBefore(AssetCoreAutoConfiguration.class)
@RequiredArgsConstructor
public class AssetS3AutoConfiguration {

    private final AssetS3Properties assetS3Properties;

    @Bean
    @ConditionalOnMissingBean
    public S3ClientProvider getS3Client() {
        return new DefaultS3ClientProvider(assetS3Properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public BucketResolver bucketResolver() {
        return new DefaultBucketResolver(assetS3Properties.getBucket());
    }

    @Bean
    @ConditionalOnMissingBean
    public PathResolver pathResolver() {
        return new DefaultPathResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public FileStorageService fileStorageService(@Autowired BucketResolver bucketResolver, @Autowired PathResolver pathResolver, @Autowired S3ClientProvider s3ClientProvider) {
        return new S3FileStoreService(bucketResolver, pathResolver, s3ClientProvider.getClient());
    }

}
