package io.rocketbase.commons.config;

import io.rocketbase.commons.service.AssetMongoRepository;
import io.rocketbase.commons.service.AssetRepository;
import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.service.MongoFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import javax.annotation.Resource;
import java.io.Serializable;

@Configuration
@AutoConfigureBefore(AssetCoreAutoConfiguration.class)
@RequiredArgsConstructor
public class AssetMongoAutoConfiguration implements Serializable {

    @Resource
    private GridFsTemplate gridFsTemplate;

    @Bean
    @ConditionalOnMissingBean
    public FileStorageService fileStorageService() {
        return new MongoFileStorageService(gridFsTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public AssetRepository assetRepository(@Autowired MongoTemplate mongoTemplate) {
        return new AssetMongoRepository(mongoTemplate);
    }

}
