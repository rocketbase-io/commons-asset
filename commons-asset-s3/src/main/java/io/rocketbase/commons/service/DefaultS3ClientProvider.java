package io.rocketbase.commons.service;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.rocketbase.commons.config.AssetS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class DefaultS3ClientProvider implements S3ClientProvider {

    private final AssetS3Properties assetS3Properties;

    @Override
    public AmazonS3 getClient() {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(assetS3Properties.getAccessKey(), assetS3Properties.getSecretKey())));

        if (!StringUtils.isEmpty(assetS3Properties.getSignerOverride())) {
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setSignerOverride(assetS3Properties.getSignerOverride());
            builder.withClientConfiguration(clientConfiguration);
        }

        if (!StringUtils.isEmpty(assetS3Properties.getEndpoint())) {
            builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(assetS3Properties.getEndpoint(), assetS3Properties.getRegion()));
        } else {
            builder.withRegion(assetS3Properties.getRegion());
        }

        if (assetS3Properties.getPathStyleAccessEnabled() != null) {
            builder.withPathStyleAccessEnabled(assetS3Properties.getPathStyleAccessEnabled());
        }
        return builder.build();
    }
}
