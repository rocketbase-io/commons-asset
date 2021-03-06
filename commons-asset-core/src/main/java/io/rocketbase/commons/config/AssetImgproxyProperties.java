package io.rocketbase.commons.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@Validated
@ConfigurationProperties(prefix = "asset.imgproxy")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetImgproxyProperties implements Serializable {

    @NotEmpty
    private String baseUrl;

    private String key;

    private String salt;
    
    private boolean enlarge = false;

}
