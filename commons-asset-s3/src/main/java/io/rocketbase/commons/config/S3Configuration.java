package io.rocketbase.commons.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.S3ClientOptions;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.lang.reflect.Field;


@Configuration
public class S3Configuration {

    @Resource
    private ApplicationContext applicationContext;

    private AmazonS3 s3Client;

    @Getter
    @Value(value = "${asset.s3.bucket}")
    private String bucketName;

    /**
     * in case we use minio or other services with different endpoints
     */
    @Value("${asset.s3.endpoint:}")
    private String endpoint;

    @SneakyThrows
    public AmazonS3 getS3Client() {
        if (s3Client == null) {
            s3Client = applicationContext.getBean(AmazonS3.class);
            if (!endpoint.isEmpty()) {
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

                s3Client.setEndpoint(endpoint);
            }
        }
        return s3Client;
    }

}
