package io.rocketbase.commons.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.dto.asset.AssetReference;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AssetEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.gridfs.GridFsUpload;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class MongoFileStorageService implements FileStorageService {

    private final GridFsTemplate gridFsTemplate;
    private final AssetApiProperties assetApiProperties;

    @SneakyThrows
    @Override
    public void upload(AssetEntity entity, File file) {
        ObjectId objectId = gridFsTemplate.store(new FileInputStream(file), entity.getOriginalFilename(),
                entity.getType()
                        .getContentType(), generateObjectMeta(entity));
        entity.setId(objectId.toHexString());
        entity.setUrlPath(objectId.toHexString());
    }

    @SneakyThrows
    @Override
    public void storePreview(AssetReference reference, File file, PreviewSize previewSize) {
        gridFsTemplate.store(GridFsUpload.fromStream(new FileInputStream(file))
                .id(buildPreviewSizeId(reference, previewSize))
                .filename(buildPreviewFilename(reference, previewSize))
                .metadata(new Document()
                        .append("preview", previewSize.name())
                        .append("assetId", reference.getId()))
                .contentType(reference.getType().getContentType())
                .build());
    }

    @Override
    public InputStreamResource download(AssetEntity entity) {
        GridFSFile gridFsFile = gridFsTemplate.findOne(getIdQuery(entity.getId()));
        if (gridFsFile == null) {
            throw new NotFoundException("asset with id " + entity.getId() + " was not found in system");
        }
        return gridFsTemplate.getResource(gridFsFile);
    }

    protected String buildPreviewSizeId(AssetReference reference, PreviewSize previewSize) {
        return reference.getId() + "-" + previewSize.name().toLowerCase();
    }

    protected String buildPreviewFilename(AssetReference reference, PreviewSize previewSize) {
        return reference.getId() + "_" + previewSize.name().toLowerCase() + reference.getType().getFileExtensionForSuffix();
    }

    @Override
    public InputStreamResource downloadPreview(AssetReference reference, PreviewSize previewSize) {
        GridFSFile gridFsFile = gridFsTemplate.findOne(getIdQuery(buildPreviewSizeId(reference, previewSize)));
        if (gridFsFile == null) {
            throw new NotFoundException("assetPreview of id " + reference.getId() + " and size " + previewSize + " was not found in system");
        }
        return gridFsTemplate.getResource(gridFsFile);
    }

    @Override
    public void delete(AssetEntity entity) {
        gridFsTemplate.delete(getIdQuery(entity.getId()));

        if (assetApiProperties.isPrecalculate()) {
            Pattern previewIdPattern = Pattern.compile(entity.getId() + "\\-(xs|s|m|l|xl)", Pattern.CASE_INSENSITIVE);
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(previewIdPattern)));
        }
    }

    @SneakyThrows
    @Override
    public void copy(AssetEntity source, AssetEntity target) {
        ObjectId objectId = gridFsTemplate.store(download(source).getInputStream(), target.getOriginalFilename(),
                target.getType()
                        .getContentType(), generateObjectMeta(target));
        target.setId(objectId.toHexString());
        target.setUrlPath(objectId.toHexString());
    }

    @Nonnull
    private Query getIdQuery(String id) {
        return new Query(Criteria.where("_id")
                .is(id));
    }

    private Document generateObjectMeta(AssetEntity entity) {
        Document meta = new Document()
                .append("type", entity.getType().name())
                .append("originalFilename", entity.getOriginalFilename())
                .append("created", entity.getCreated().toString());
        if (entity.getSystemRefId() != null) {
            meta.append("systemRefId", entity.getSystemRefId());
        }
        if (entity.getReferenceUrl() != null) {
            meta.append("referenceUrl", entity.getReferenceUrl());
        }
        return meta;
    }
}
