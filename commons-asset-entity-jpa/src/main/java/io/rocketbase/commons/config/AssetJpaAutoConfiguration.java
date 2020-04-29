package io.rocketbase.commons.config;

import io.rocketbase.commons.repository.AssetEntityRepository;
import io.rocketbase.commons.service.AssetJpaRepository;
import io.rocketbase.commons.service.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Configuration
@AutoConfigureBefore(AssetCoreAutoConfiguration.class)
@RequiredArgsConstructor
public class AssetJpaAutoConfiguration implements Serializable {

    @Bean
    @ConditionalOnMissingBean
    public AssetRepository assetRepository(@Autowired AssetEntityRepository assetEntityRepository) {
        return new AssetJpaRepository(assetEntityRepository);
    }

}
