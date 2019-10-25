package io.rocketbase.commons.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import io.rocketbase.commons.model.AssetEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;

@RequiredArgsConstructor
public class MongoFileStorageService implements FileStorageService {


    private final GridFsTemplate gridFsTemplate;

    @SneakyThrows
    @Override
    public void upload(AssetEntity entity, File file) {
        ObjectId objectId = gridFsTemplate.store(new FileInputStream(file), entity.getOriginalFilename(),
                entity.getType().getContentType(), generateObjectMeta(entity));
        entity.setId(objectId.toHexString());
        entity.setUrlPath(objectId.toHexString());
    }

    @Override
    public InputStreamResource download(AssetEntity entity) {
        GridFSFile gridFsFile = gridFsTemplate.findOne(getIdQuery(entity.getId()));
        return gridFsTemplate.getResource(gridFsFile.getFilename());
    }

    @Override
    public void delete(AssetEntity entity) {
        gridFsTemplate.delete(getIdQuery(entity.getId()));
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
