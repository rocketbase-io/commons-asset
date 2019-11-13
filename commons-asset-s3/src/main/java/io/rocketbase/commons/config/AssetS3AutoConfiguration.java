package io.rocketbase.commons.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.S3ClientOptions;
import io.rocketbase.commons.service.BucketResolver;
import io.rocketbase.commons.service.DefaultBucketResolver;
import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.service.S3FileStoreService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.lang.reflect.Field;


@Configuration
@EnableConfigurationProperties({S3Properties.class})
@AutoConfigureBefore(AssetCoreAutoConfiguration.class)
@RequiredArgsConstructor
public class AssetS3AutoConfiguration {

    private final S3Properties s3Properties;

    @Resource
    private ApplicationContext applicationContext;

    private AmazonS3 s3Client;


    @SneakyThrows
    public AmazonS3 getS3Client() {
        if (s3Client == null) {
            s3Client = applicationContext.getBean(AmazonS3.class);
            if (!s3Properties.getEndpoint().isEmpty()) {
                Class<? extends AmazonS3> s3ClientClass = s3Client.getClass();
                Field isImmutable = s3ClientClass.getSuperclass()
                        .getDeclaredField("isImmutable");
                if (isImmutable != null) {
                    isImmutable.setAccessible(true);
                    isImmutable.setBoolean(s3Client, false);
                }
                s3Client.setS3ClientOptions(S3ClientOptions.builder()
                        .setPathStyleAccess(true)
                        .build());

                s3Client.setEndpoint(s3Properties.getEndpoint());
            }
        }
        return s3Client;
    }

    @Bean
    @ConditionalOnMissingBean
    public BucketResolver bucketResolver() {
        return new DefaultBucketResolver(s3Properties.getBucket());
    }


    @Bean
    @ConditionalOnMissingBean
    public FileStorageService fileStorageService(@Autowired BucketResolver bucketResolver) {
        return new S3FileStoreService(bucketResolver, getS3Client());
    }

}
