package io.rocketbase.commons.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import io.rocketbase.commons.config.AssetS3Properties;
import io.rocketbase.commons.dto.asset.AssetReferenceType;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.util.Nulls;
import io.rocketbase.commons.util.UrlParts;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;

import java.io.File;
import java.io.FileInputStream;
import java.text.Normalizer;
import java.util.Date;

public class S3FileStoreService implements FileStorageService {

    private final AssetS3Properties assetS3Properties;
    private final BucketResolver bucketResolver;
    private final PathResolver pathResolver;
    private final AmazonS3 amazonS3;

    public S3FileStoreService(AssetS3Properties assetS3Properties, BucketResolver bucketResolver, PathResolver pathResolver, AmazonS3 amazonS3) {
        this.assetS3Properties = assetS3Properties;
        this.bucketResolver = bucketResolver;
        this.pathResolver = pathResolver;
        this.amazonS3 = amazonS3;
    }

    @SneakyThrows
    @Override
    public void upload(AssetEntity entity, File file) {
        entity.setUrlPath(pathResolver.getAbsolutePath(entity));

        TransferManager transferManager = TransferManagerBuilder.standard()
                .withS3Client(amazonS3)
                .build();
        ObjectMetadata objectMetadata = generateObjectMeta(entity);
        Upload upload = transferManager.upload(new PutObjectRequest(bucketResolver.resolveBucketName(entity),
                entity.getUrlPath(), file)
                .withMetadata(objectMetadata)
                .withCannedAcl(assetS3Properties.isPublicReadObject() ? CannedAccessControlList.PublicRead : CannedAccessControlList.BucketOwnerRead));

        UploadResult uploadResult = upload.waitForUploadResult();

    }

    @SneakyThrows
    @Override
    public InputStreamResource download(AssetEntity entity) {
        File tempFile = File.createTempFile("asset", entity.getType().getFileExtensionForSuffix());
        // not the best cleanup...
        tempFile.deleteOnExit();

        TransferManager transferManager = TransferManagerBuilder.standard()
                .withS3Client(amazonS3)
                .build();
        Download download = transferManager.download(bucketResolver.resolveBucketName(entity), entity.getUrlPath(), tempFile);
        download.waitForCompletion();

        return new InputStreamResource(new FileInputStream(tempFile));
    }

    @SneakyThrows
    @Override
    public String getDownloadUrl(AssetReferenceType reference) {
        if (assetS3Properties.getDownloadExpire() > 0) {
            Date expiration = new Date(new Date().getTime() + 1000 * 60 * assetS3Properties.getDownloadExpire());
            return amazonS3.generatePresignedUrl(bucketResolver.resolveBucketName(reference), reference.getUrlPath(), expiration).toString();
        }
        return buildPublicUrl(reference);
    }

    protected String buildPublicUrl(AssetReferenceType reference) {
        return UrlParts.ensureEndsWithSlash(assetS3Properties.getPublicBaseUrl()) + bucketResolver.resolveBucketName(reference) + UrlParts.ensureStartsWithSlash(reference.getUrlPath());
    }

    @Override
    public void delete(AssetEntity entity) {
        amazonS3.deleteObject(bucketResolver.resolveBucketName(entity), entity.getUrlPath());
    }

    @Override
    public void copy(AssetEntity source, AssetEntity target) {
        target.setUrlPath(pathResolver.getAbsolutePath(target));
        amazonS3.copyObject(bucketResolver.resolveBucketName(source), source.getUrlPath(), bucketResolver.resolveBucketName(target), target.getUrlPath());
    }

    private ObjectMetadata generateObjectMeta(AssetEntity entity) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(entity.getType().getContentType());
        objectMetadata.setHeader("type", entity.getType().name());
        objectMetadata.setHeader("context", Nulls.notNull(entity.getContext()));
        objectMetadata.setHeader("originalFilename", cleanString(Nulls.notNull(entity.getOriginalFilename())));
        objectMetadata.setHeader("created", entity.getCreated().toString());
        if (entity.getSystemRefId() != null) {
            objectMetadata.setHeader("systemRefId", entity.getSystemRefId());
        }
        if (entity.getReferenceUrl() != null) {
            objectMetadata.setHeader("referenceUrl", cleanString(entity.getReferenceUrl()));
        }

        return objectMetadata;
    }

    String cleanString(String input) {
        String result = Normalizer.normalize(input, Normalizer.Form.NFD);
        result = result.replaceAll("[^\\x00-\\x7F]", "");
        return result;
    }
}
