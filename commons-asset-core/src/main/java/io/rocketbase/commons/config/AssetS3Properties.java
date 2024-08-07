package io.rocketbase.commons.config;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.Duration;

@Data
@Validated
@ConfigurationProperties(prefix = "asset.s3")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetS3Properties implements Serializable {

    @NotEmpty
    private String bucket;

    private String accessKey;

    private String secretKey;

    private String region;

    private String endpoint;

    private Boolean pathStyleAccessEnabled;

    private String signerOverride;

    /**
     * expire for pre-signed URL in minutes (AWS/Minio max 7 days)<br>
     * 0 means object should be public accessible
     */
    private Duration downloadExpire = Duration.ofDays(3);

    /**
     * when enabled for each uploaded object the acl will get set CannedAccessControlList.PublicRead<br>
     * by default CannedAccessControlList.BucketOwnerRead
     */
    private boolean publicReadObject = false;


    public String getPublicBaseUrl() {
        if (!StringUtils.isEmpty(getEndpoint())) {
            return getEndpoint();
        } else {
            return "https://s3." + getRegion().toLowerCase() + ".amazonaws.com";
        }
    }

}
