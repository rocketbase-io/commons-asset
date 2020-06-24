package io.rocketbase.commons.service;

import com.amazonaws.services.s3.AmazonS3;

public interface S3ClientProvider {

    AmazonS3 getClient();

}
