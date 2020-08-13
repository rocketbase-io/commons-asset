package io.rocketbase.commons.config;

import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.service.JpaFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import java.io.Serializable;

@Configuration
@AutoConfigureBefore(AssetCoreAutoConfiguration.class)
@EnableConfigurationProperties({AssetApiProperties.class})
@RequiredArgsConstructor
public class AssetJpaStorageAutoConfiguration implements Serializable {

    private final AssetApiProperties assetApiProperties;

    @Bean
    @ConditionalOnMissingBean
    public FileStorageService fileStorageService(@Autowired EntityManager entityManager) {
        return new JpaFileStorageService(entityManager, assetApiProperties);
    }

}
