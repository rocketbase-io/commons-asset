package io.rocketbase.commons.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@Validated
@ConfigurationProperties(prefix = "asset.s3")
public class S3Properties {

    @NotEmpty
    private String bucket;

    private String endpoint = "";
}
