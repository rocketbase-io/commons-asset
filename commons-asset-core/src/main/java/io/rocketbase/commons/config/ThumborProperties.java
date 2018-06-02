package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "asset.thumbor")
public class ThumborProperties {

    private String host = "http://localhost";

    private String key = "";
}
