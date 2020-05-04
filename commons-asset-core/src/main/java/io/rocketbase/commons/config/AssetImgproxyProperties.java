package io.rocketbase.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@Validated
@ConfigurationProperties(prefix = "asset.imgproxy")
public class AssetImgproxyProperties implements Serializable {

    @NotEmpty
    private String baseUrl;

    private String key;

    private String salt;

}
