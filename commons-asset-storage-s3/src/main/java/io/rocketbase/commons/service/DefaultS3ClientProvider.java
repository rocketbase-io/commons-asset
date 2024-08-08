package io.rocketbase.commons.service;

import io.rocketbase.commons.config.AssetS3Properties;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.signer.AsyncAws4Signer;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.Aws4UnsignedPayloadSigner;
import software.amazon.awssdk.auth.signer.AwsS3V4Signer;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

public class DefaultS3ClientProvider implements S3ClientProvider {

    private final AssetS3Properties assetS3Properties;

    private final AwsCredentialsProvider awsCredentialsProvider;

    public DefaultS3ClientProvider(AssetS3Properties assetS3Properties) {
        this.assetS3Properties = assetS3Properties;
        this.awsCredentialsProvider = () -> AwsBasicCredentials.create(assetS3Properties.getAccessKey(), assetS3Properties.getSecretKey());
    }

    @Override
    public S3Client getClient() {
        S3ClientBuilder builder = S3Client.builder();

        if (!StringUtils.isEmpty(assetS3Properties.getEndpoint())) {
                builder.endpointOverride(URI.create(assetS3Properties.getEndpoint()));
        }
        builder.region(Region.of(assetS3Properties.getRegion()));
        if (assetS3Properties.getPathStyleAccessEnabled() != null) {
            builder.forcePathStyle(assetS3Properties.getPathStyleAccessEnabled());
        }
        if (!StringUtils.isEmpty(assetS3Properties.getSignerOverride())) {
            Signer signer;
            String signerCompare = assetS3Properties.getSignerOverride().toLowerCase();
            if (signerCompare.contains("awss3v4signer")) {
                signer = AwsS3V4Signer.create();
            } else if (signerCompare.contains("aws4unsignedpayloadsigner")) {
                signer = Aws4UnsignedPayloadSigner.create();
            } else if (signerCompare.contains("aws4signer")) {
                signer = Aws4Signer.create();
            } else if (signerCompare.contains("asyncaws4signer")) {
                signer = AsyncAws4Signer.create();
            } else {
                throw new IllegalArgumentException("not supported asset.s3.signerOverride value: " + assetS3Properties.getSignerOverride());
            }

            builder.overrideConfiguration(c -> {
                c.putAdvancedOption(SdkAdvancedClientOption.SIGNER, signer);
            });
        }
        builder.credentialsProvider(awsCredentialsProvider);
        return builder.build();
    }

    @Override
    public S3Presigner getPresigner() {
        S3Presigner.Builder builder = S3Presigner.builder()
                .credentialsProvider(awsCredentialsProvider);
        if (!StringUtils.isEmpty(assetS3Properties.getEndpoint())) {
            builder.endpointOverride(URI.create(assetS3Properties.getEndpoint()));
        }
        builder.region(Region.of(assetS3Properties.getRegion()));
        return builder.build();
    }
}
