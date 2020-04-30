package io.rocketbase.commons.service;

import io.rocketbase.asset.imgproxy.Signature;
import io.rocketbase.asset.imgproxy.SignatureConfiguration;
import io.rocketbase.asset.imgproxy.options.ResizeType;
import io.rocketbase.commons.config.AssetImgproxyProperties;
import io.rocketbase.commons.config.AssetS3Properties;
import io.rocketbase.commons.converter.AssetPreviewService;
import io.rocketbase.commons.dto.asset.AssetReferenceType;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.PreviewSize;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ImgproxyS3AssetPreviewService implements AssetPreviewService {

    final AssetImgproxyProperties imgproxyProperties;
    final AssetS3Properties s3Properties;
    final BucketResolver bucketResolver;

    @Override
    public boolean isPreviewSupported(AssetType assetType) {
        return assetType.isImage();
    }

    @Override
    public String getPreviewUrl(AssetReferenceType assetReferenceType, PreviewSize previewSize) {
        Signature signature = Signature.of(new SignatureConfiguration(imgproxyProperties.getBaseUrl(),
                imgproxyProperties.getKey(),
                imgproxyProperties.getSalt()))
                .resize(ResizeType.fit, previewSize.getMaxWidth(), previewSize.getMaxHeight(), true);
        return signature.url("s3://" + getBucket(assetReferenceType) + "/" + assetReferenceType.getUrlPath());
    }

    protected String getBucket(AssetReferenceType assetReferenceType) {
        return bucketResolver != null ? bucketResolver.resolveBucketName(assetReferenceType) : s3Properties.getBucket();
    }

    @Override
    public String getDownloadUrl(AssetReferenceType assetReferenceType) {
        Signature signature = Signature.of(new SignatureConfiguration(imgproxyProperties.getBaseUrl(),
                imgproxyProperties.getKey(),
                imgproxyProperties.getSalt()));
        return signature.url("s3://" + getBucket(assetReferenceType) + "/" + assetReferenceType.getUrlPath());
    }
}
