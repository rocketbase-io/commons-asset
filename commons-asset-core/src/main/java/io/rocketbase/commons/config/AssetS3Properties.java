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

    private String accessKey;

    private String secretKey;

    private String region;

    private String endpoint;

    private Boolean pathStyleAccessEnabled;

    private String signerOverride;

    /**
     * expire for pre-signed URL in minutes (AWS/Minio max 7 days)<br>
     * 0 means object should be public accessable<br>
     * 5 days by default
     */
    private int downloadExpire = 60 * 24 * 5;

    /**
     * when enabled for each uploaded object the acl will get set CannedAccessControlList.PublicRead<br>
     * by default CannedAccessControlList.BucketOwnerRead
     */
    private boolean publicReadObject = false;

}
