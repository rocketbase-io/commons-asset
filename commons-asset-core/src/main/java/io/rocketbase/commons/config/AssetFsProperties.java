package io.rocketbase.commons.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Data
@Validated
@ConfigurationProperties(prefix = "asset.fs")
public class AssetFsProperties implements Serializable {

    private String basePath;
}
