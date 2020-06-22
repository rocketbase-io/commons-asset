package io.rocketbase.commons.service;

import com.amazonaws.services.s3.AmazonS3;

public interface S3ClientProvider {

    AmazonS3 getClient();

    /**
     * generate related to the configuration the base public url
     * <ul>
     *     <li>https://s3.eu-central-1.amazonaws.com</li>
     *     <li>https://mini.yourdomain.com</li>
     * </ul>
     */
    String getPublicBaseUrl();

}
