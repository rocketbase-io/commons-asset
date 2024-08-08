package io.rocketbase.commons.service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

public interface S3ClientProvider {

    S3Client getClient();

    S3Presigner getPresigner();

}
