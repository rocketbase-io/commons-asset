package io.rocketbase.commons.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@Validated
@ConfigurationProperties(prefix = "asset.s3")
public class AssetS3Properties implements Serializable {

    @NotEmpty
    private String bucket;

    @NotEmpty
    private String accessKey;

    @NotEmpty
    private String secretKey;

    @NotEmpty
    private String region;

    private String endpoint;

    private Boolean instanceProfile;

    private Boolean pathStyleAccessEnabled;

    private String signerOverride;

}
