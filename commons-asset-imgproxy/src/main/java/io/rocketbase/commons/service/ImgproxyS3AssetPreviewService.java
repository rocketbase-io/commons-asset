package io.rocketbase.commons.service;

import io.rocketbase.asset.imgproxy.Signature;
import io.rocketbase.asset.imgproxy.SignatureConfiguration;
import io.rocketbase.asset.imgproxy.options.ResizeType;
import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.config.AssetImgproxyProperties;
import io.rocketbase.commons.config.AssetS3Properties;
import io.rocketbase.commons.converter.AbstractAssetPreviewService;
import io.rocketbase.commons.dto.asset.AssetReferenceType;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.PreviewSize;

public class ImgproxyS3AssetPreviewService extends AbstractAssetPreviewService {

    final AssetImgproxyProperties imgproxyProperties;
    final AssetS3Properties s3Properties;
    final BucketResolver bucketResolver;

    public ImgproxyS3AssetPreviewService(AssetApiProperties assetApiProperties, AssetImgproxyProperties imgproxyProperties, AssetS3Properties s3Properties, BucketResolver bucketResolver, FileStorageService fileStorageService) {
        super(assetApiProperties, fileStorageService, true);
        this.imgproxyProperties = imgproxyProperties;
        this.s3Properties = s3Properties;
        this.bucketResolver = bucketResolver;
    }

    @Override
    protected String getBaseUrl() {
        return null;
    }

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

}
