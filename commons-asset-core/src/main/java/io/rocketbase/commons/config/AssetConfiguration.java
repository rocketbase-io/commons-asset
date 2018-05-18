package io.rocketbase.commons.config;

import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.service.MongoFileStorageService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import javax.annotation.Resource;

@Data
@Configuration
public class AssetConfiguration {

    @Value("${asset.api.endpoint:/api/asset}")
    private String apiEndpoint;

    @Value("${asset.api.render:/get/asset}")
    private String renderEndpoint;

    @Value(value = "${asset.thumbor.host:http://localhost}")
    private String thumborHost;

    @Value(value = "${asset.thumbor.key:}")
    private String thumborKey;

    @Resource
    private GridFsTemplate gridFsTemplate;

    @Bean
    @ConditionalOnMissingBean
    public FileStorageService fileStorageService() {
        return new MongoFileStorageService(gridFsTemplate);
    }

}
