package io.rocketbase.commons.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import io.rocketbase.commons.config.AssetS3Properties;
import io.rocketbase.commons.dto.asset.AssetReference;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.util.Nulls;
import io.rocketbase.commons.util.UrlParts;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;

import java.io.File;
import java.io.FileInputStream;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

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

        TransferManager transferManager = getTransferManager();

        ObjectMetadata objectMetadata = generateObjectMeta(entity);
        transferManager.upload(new PutObjectRequest(bucketResolver.resolveBucketName(entity),
                entity.getUrlPath(), file)
                .withMetadata(objectMetadata)
                .withCannedAcl(assetS3Properties.isPublicReadObject() ? CannedAccessControlList.PublicRead : CannedAccessControlList.BucketOwnerRead))
                .waitForUploadResult();
    }

    @SneakyThrows
    @Override
    public void storePreview(AssetReference reference, File file, PreviewSize previewSize) {
        TransferManager transferManager = getTransferManager();

        ObjectMetadata objectMetadata = generateObjectMeta(reference);
        transferManager.upload(new PutObjectRequest(bucketResolver.resolveBucketName(reference),
                buildPreviewPart(reference, previewSize), file)
                .withMetadata(objectMetadata)
                .withCannedAcl(assetS3Properties.isPublicReadObject() ? CannedAccessControlList.PublicRead : CannedAccessControlList.BucketOwnerRead))
                .waitForUploadResult();
    }

    protected TransferManager getTransferManager() {
        return TransferManagerBuilder.standard()
                .withS3Client(amazonS3)
                .build();
    }

    @SneakyThrows
    @Override
    public InputStreamResource download(AssetEntity entity) {
        return download(entity, entity.getUrlPath());
    }

    @Override
    public InputStreamResource downloadPreview(AssetReference reference, PreviewSize previewSize) {
        return download(reference, buildPreviewPart(reference, previewSize));
    }

    @SneakyThrows
    protected InputStreamResource download(AssetReference reference, String url) {
        File tempFile = File.createTempFile("asset", reference.getType().getFileExtensionForSuffix());
        // not the best cleanup...
        tempFile.deleteOnExit();

        TransferManager transferManager = getTransferManager();
        Download download = transferManager.download(bucketResolver.resolveBucketName(reference), url, tempFile);
        download.waitForCompletion();

        return new InputStreamResource(new FileInputStream(tempFile));
    }

    @SneakyThrows
    @Override
    public String getDownloadUrl(AssetReference reference) {
        return getDownloadUrl(reference, reference.getUrlPath());
    }

    @Override
    public String getDownloadPreviewUrl(AssetReference reference, PreviewSize previewSize) {
        return getDownloadUrl(reference, buildPreviewPart(reference, previewSize));
    }

    protected String buildPreviewPart(AssetReference reference, PreviewSize previewSize) {
        return previewSize.getPreviewStoragePath() + "/" + reference.getUrlPath();
    }

    protected String getDownloadUrl(AssetReference reference, String url) {
        if (assetS3Properties.getDownloadExpire() > 0) {
            Date expiration = new Date(new Date().getTime() + 1000 * 60 * assetS3Properties.getDownloadExpire());
            return amazonS3.generatePresignedUrl(bucketResolver.resolveBucketName(reference), url, expiration).toString();
        }
        return buildPublicUrl(reference, url);
    }

    protected String buildPublicUrl(AssetReference reference, String url) {
        return UrlParts.ensureEndsWithSlash(assetS3Properties.getPublicBaseUrl()) + bucketResolver.resolveBucketName(reference) + UrlParts.ensureStartsWithSlash(url);
    }

    @Override
    public void delete(AssetEntity entity) {
        amazonS3.deleteObject(bucketResolver.resolveBucketName(entity), entity.getUrlPath());

        DeleteObjectsRequest objectsRequest = new DeleteObjectsRequest(bucketResolver.resolveBucketName(entity));
        objectsRequest.setKeys(Arrays.stream(PreviewSize.values())
                .map(size -> new DeleteObjectsRequest.KeyVersion(buildPreviewPart(entity, size)))
                .collect(Collectors.toList()));
        objectsRequest.setQuiet(true);
        amazonS3.deleteObjects(objectsRequest);
    }

    @Override
    public void copy(AssetEntity source, AssetEntity target) {
        target.setUrlPath(pathResolver.getAbsolutePath(target));
        amazonS3.copyObject(bucketResolver.resolveBucketName(source), source.getUrlPath(), bucketResolver.resolveBucketName(target), target.getUrlPath());
    }

    private ObjectMetadata generateObjectMeta(AssetReference reference) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(reference.getType().getContentType());
        objectMetadata.setHeader("type", reference.getType().name());
        objectMetadata.setHeader("context", Nulls.notNull(reference.getContext()));
        objectMetadata.setHeader("originalFilename", cleanString(Nulls.notNull(reference.getMeta().getOriginalFilename())));
        objectMetadata.setHeader("created", reference.getMeta().getCreated().toString());
        return objectMetadata;
    }

    private ObjectMetadata generateObjectMeta(AssetEntity entity) {
        ObjectMetadata objectMetadata = generateObjectMeta((AssetReference) entity);
        if (entity.getSystemRefId() != null) {
            objectMetadata.setHeader("systemRefId", entity.getSystemRefId());
        }
        if (entity.getReferenceUrl() != null) {
            objectMetadata.setHeader("referenceUrl", cleanString(entity.getReferenceUrl()));
        }
        return objectMetadata;
    }

    @Override
    public boolean useDownloadPreviewUrl() {
        return true;
    }

    String cleanString(String input) {
        String result = Normalizer.normalize(input, Normalizer.Form.NFD);
        result = result.replaceAll("[^\\x00-\\x7F]", "");
        return result;
    }
}
