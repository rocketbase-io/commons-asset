package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "asset.thumbor")
public class ThumborProperties implements Serializable {

    private String host = "http://localhost";

    private String key = "";
}
