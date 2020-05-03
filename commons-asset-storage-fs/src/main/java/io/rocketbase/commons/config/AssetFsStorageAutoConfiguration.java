package io.rocketbase.commons.config;

import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.service.FsFileStorageService;
import io.rocketbase.commons.service.PathResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Configuration
@EnableConfigurationProperties({AssetFsProperties.class})
@AutoConfigureBefore(AssetCoreAutoConfiguration.class)
@RequiredArgsConstructor
public class AssetFsStorageAutoConfiguration implements Serializable {

    private final AssetFsProperties assetFsProperties;

    @Bean
    @ConditionalOnMissingBean
    public FileStorageService fileStorageService(@Autowired PathResolver pathResolver) {
        return new FsFileStorageService(assetFsProperties.getBasePath(), pathResolver);
    }

}
