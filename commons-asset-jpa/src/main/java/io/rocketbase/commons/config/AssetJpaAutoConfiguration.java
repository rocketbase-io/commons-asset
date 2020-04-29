package io.rocketbase.commons.config;

import io.rocketbase.commons.repository.AssetEntityRepository;
import io.rocketbase.commons.service.AssetJpaRepository;
import io.rocketbase.commons.service.AssetRepository;
import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.service.JpaFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import java.io.Serializable;

@Configuration
@AutoConfigureBefore(AssetCoreAutoConfiguration.class)
@RequiredArgsConstructor
public class AssetJpaAutoConfiguration implements Serializable {

    @Bean
    @ConditionalOnMissingClass(value = {"io.rocketbase.commons.config.AssetS3Properties"})
    @ConditionalOnMissingBean
    public FileStorageService fileStorageService(@Autowired EntityManager entityManager) {
        return new JpaFileStorageService(entityManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetRepository assetRepository(@Autowired AssetEntityRepository assetEntityRepository) {
        return new AssetJpaRepository(assetEntityRepository);
    }

}
