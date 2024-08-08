package io.rocketbase.commons.service;

import io.rocketbase.commons.config.AssetS3Properties;
import io.rocketbase.commons.dto.asset.AssetReference;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.util.Nulls;
import io.rocketbase.commons.util.UrlParts;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class S3FileStoreService implements FileStorageService {

    private final AssetS3Properties assetS3Properties;
    private final BucketResolver bucketResolver;
    private final PathResolver pathResolver;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public S3FileStoreService(AssetS3Properties assetS3Properties, BucketResolver bucketResolver, PathResolver pathResolver, S3Client s3Client, S3Presigner s3Presigner) {
        this.assetS3Properties = assetS3Properties;
        this.bucketResolver = bucketResolver;
        this.pathResolver = pathResolver;
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @SneakyThrows
    @Override
    public void upload(AssetEntity entity, File file) {
        entity.setUrlPath(pathResolver.getAbsolutePath(entity));
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(this.bucketResolver.resolveBucketName(entity))
                .key(entity.getUrlPath())
                .metadata(generateObjectMeta(entity))
                .contentType(entity.getType().getContentType())
                .acl(assetS3Properties.isPublicReadObject() ? ObjectCannedACL.PUBLIC_READ : ObjectCannedACL.BUCKET_OWNER_READ)
                .build();

        s3Client.putObject(putObjectRequest, file.toPath());
    }

    @SneakyThrows
    @Override
    public void storePreview(AssetReference reference, File file, PreviewSize previewSize) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(this.bucketResolver.resolveBucketName(reference))
                .key(buildPreviewPart(reference, previewSize))
                .metadata(generateReferenceMeta(reference))
                .contentType(reference.getType().getContentType())
                .acl(assetS3Properties.isPublicReadObject() ? ObjectCannedACL.PUBLIC_READ : ObjectCannedACL.BUCKET_OWNER_READ)
                .build();

        s3Client.putObject(putObjectRequest, file.toPath());
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

        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketResolver.resolveBucketName(reference))
                .key(url)
                .build();

        ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(objectRequest);
        return new InputStreamResource(responseBytes.asInputStream());
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
        if (!assetS3Properties.isPublicReadObject()) {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucketResolver.resolveBucketName(reference))
                    .key(url)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(assetS3Properties.getDownloadExpire())
                    .getObjectRequest(objectRequest)
                    .build();


            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedGetObjectRequest.url().toExternalForm();
        }
        return buildPublicUrl(reference, url);
    }

    protected String buildPublicUrl(AssetReference reference, String url) {
        return UrlParts.ensureEndsWithSlash(assetS3Properties.getPublicBaseUrl()) + bucketResolver.resolveBucketName(reference) + UrlParts.ensureStartsWithSlash(url);
    }

    @Override
    public void delete(AssetEntity entity) {
        ArrayList<ObjectIdentifier> toDelete = new ArrayList<ObjectIdentifier>();
        toDelete.add(ObjectIdentifier.builder().key(entity.getUrlPath()).build());
        Arrays.stream(PreviewSize.values()).forEach(size -> toDelete.add(ObjectIdentifier.builder().key(buildPreviewPart(entity, size)).build()));

        s3Client.deleteObjects(DeleteObjectsRequest.builder()
                .bucket(bucketResolver.resolveBucketName(entity))
                        .delete(Delete.builder().objects(toDelete).build())
                .build());
    }

    @Override
    public void copy(AssetEntity source, AssetEntity target) {
        target.setUrlPath(pathResolver.getAbsolutePath(target));
        s3Client.copyObject(CopyObjectRequest.builder()
                        .sourceBucket(bucketResolver.resolveBucketName(source))
                        .sourceKey(source.getUrlPath())
                        .destinationBucket(bucketResolver.resolveBucketName(target))
                        .destinationKey(target.getUrlPath())
                        .build());
    }

private Map<String, String> generateReferenceMeta(AssetReference entity) {
    Map<String, String> objectMetadata = new HashMap<>();

    objectMetadata.put("type", entity.getType().name());
    objectMetadata.put("context", Nulls.notNull(entity.getContext()));
    objectMetadata.put("originalFilename", cleanString(Nulls.notNull(entity.getMeta().getOriginalFilename())));
    objectMetadata.put("created", entity.getMeta().getCreated().toString());

    if (entity.getSystemRefId() != null) {
        objectMetadata.put("systemRefId", entity.getSystemRefId());
    }
    return objectMetadata;
}

private Map<String, String> generateObjectMeta(AssetEntity entity) {
    Map<String, String> objectMetadata = generateReferenceMeta((AssetReference) entity);

    if (entity.getReferenceUrl() != null) {
        objectMetadata.put("referenceUrl", cleanString(entity.getReferenceUrl()));
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
