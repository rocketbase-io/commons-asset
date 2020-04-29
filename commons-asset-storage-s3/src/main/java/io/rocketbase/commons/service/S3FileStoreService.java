package io.rocketbase.commons.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.util.Nulls;
import io.rocketbase.commons.util.UrlParts;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;

import java.io.File;
import java.io.FileInputStream;

@RequiredArgsConstructor
public class S3FileStoreService implements FileStorageService {

    private final BucketResolver bucketResolver;
    private final PathResolver pathResolver;
    private final AmazonS3 amazonS3;

    @SneakyThrows
    @Override
    public void upload(AssetEntity entity, File file) {
        entity.setUrlPath(String.format("%s%s.%s", UrlParts.ensureEndsWithSlash(pathResolver.resolvePath(entity)), entity.getId(), entity.getType().getFileExtension())
                .toLowerCase());

        TransferManager transferManager = TransferManagerBuilder.standard()
                .withS3Client(amazonS3)
                .build();
        ObjectMetadata objectMetadata = generateObjectMeta(entity);
        Upload upload = transferManager.upload(new PutObjectRequest(bucketResolver.resolveBucketName(entity),
                entity.getUrlPath(), new FileInputStream(file), objectMetadata)
                .withCannedAcl(CannedAccessControlList.BucketOwnerRead));

        upload.waitForUploadResult();
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

    @Override
    public void delete(AssetEntity entity) {
        amazonS3.deleteObject(bucketResolver.resolveBucketName(entity), entity.getUrlPath());
    }

    private ObjectMetadata generateObjectMeta(AssetEntity entity) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(entity.getType().getContentType());
        objectMetadata.setHeader("type", entity.getType().name());
        objectMetadata.setHeader("context", Nulls.notNull(entity.getContext()));
        objectMetadata.setHeader("originalFilename", Nulls.notNull(entity.getOriginalFilename()));
        objectMetadata.setHeader("created", entity.getCreated().toString());
        if (entity.getSystemRefId() != null) {
            objectMetadata.setHeader("systemRefId", entity.getSystemRefId());
        }
        if (entity.getReferenceUrl() != null) {
            objectMetadata.setHeader("referenceUrl", entity.getReferenceUrl());
        }

        return objectMetadata;
    }
}
