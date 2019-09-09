package io.rocketbase.commons.config;

import io.rocketbase.commons.repository.AssetEntityRepository;
import io.rocketbase.commons.service.AssetJpaRepository;
import io.rocketbase.commons.service.AssetRepository;
import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.service.JpaFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import java.io.Serializable;

@Configuration
@RequiredArgsConstructor
public class AssetJpaAutoConfiguration implements Serializable {

    @Bean
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
