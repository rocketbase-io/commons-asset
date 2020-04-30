package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "asset.imgproxy")
public class AssetImgproxyProperties implements Serializable {

    private String baseUrl;

    private String key;

    private String salt;

}
