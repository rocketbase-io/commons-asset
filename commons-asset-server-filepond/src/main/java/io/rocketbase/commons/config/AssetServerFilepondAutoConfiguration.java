package io.rocketbase.commons.config;

import io.rocketbase.commons.controller.AssetFilepondUploadController;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Configuration
@RequiredArgsConstructor
public class AssetServerFilepondAutoConfiguration implements Serializable {

    @Bean
    @ConditionalOnMissingBean
    public AssetFilepondUploadController assetFilepondUploadController() {
        return new AssetFilepondUploadController();
    }

}
