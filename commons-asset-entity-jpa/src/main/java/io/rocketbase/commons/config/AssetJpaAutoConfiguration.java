package io.rocketbase.commons.config;

import io.rocketbase.commons.service.AssetJpaRepository;
import io.rocketbase.commons.service.AssetRepository;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Optional;

@Configuration
@AutoConfigureBefore(AssetCoreAutoConfiguration.class)
@RequiredArgsConstructor
public class AssetJpaAutoConfiguration implements Serializable {

    @Bean
    @ConditionalOnMissingBean
    public AssetRepository assetRepository(@Autowired EntityManager entityManager, @Autowired(required = false) AuditorAware auditorAware) {
        return new AssetJpaRepository(entityManager, Nulls.notNull(auditorAware, () -> Optional.of("")));
    }

}
