package io.rocketbase.commons.config;

import io.rocketbase.commons.service.AssetMongoRepository;
import io.rocketbase.commons.service.AssetRepository;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Optional;

@Configuration
@AutoConfigureBefore(AssetCoreAutoConfiguration.class)
@RequiredArgsConstructor
public class AssetMongoAutoConfiguration implements Serializable {

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private MongoMappingContext mongoMappingContext;

    @Value("${asset.api.mongo.index:true}")
    private boolean mongoEnsureIndex;

    @Bean
    @ConditionalOnMissingBean
    public AssetRepository assetRepository(@Autowired(required = false) AuditorAware auditorAware) {
        return new AssetMongoRepository(mongoTemplate, mongoMappingContext, mongoEnsureIndex, Nulls.notNull(auditorAware, () -> Optional.of("")));
    }

}
