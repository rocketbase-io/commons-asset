package io.rocketbase.commons.service;

import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.dto.asset.AssetReference;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.model.AssetFileEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@RequiredArgsConstructor
@Transactional
public class JpaFileStorageService implements FileStorageService {

    private final EntityManager entityManager;
    private final AssetApiProperties assetApiProperties;

    @SneakyThrows
    @Override
    public void upload(AssetEntity entity, File file) {
        AssetFileEntity assetFileEntity = AssetFileEntity.builder()
                .id(entity.getId())
                .binary(new FileInputStream(file).readAllBytes())
                .build();
        entityManager.persist(assetFileEntity);
        entity.setUrlPath(entity.getId());
    }

    @SneakyThrows
    @Override
    public void storePreview(AssetReference reference, File file, PreviewSize previewSize) {
        AssetFileEntity assetFileEntity = AssetFileEntity.builder()
                .id(buildPreviewSizeId(reference, previewSize))
                .binary(new FileInputStream(file).readAllBytes())
                .build();
        entityManager.persist(assetFileEntity);
    }

    protected String buildPreviewSizeId(AssetReference reference, PreviewSize previewSize) {
        return reference.getId() + "-" + previewSize.name().toLowerCase();
    }

    @SneakyThrows
    @Override
    public InputStreamResource download(AssetEntity entity) {
        AssetFileEntity assetFileEntity = entityManager.find(AssetFileEntity.class, entity.getId());
        if (assetFileEntity == null) {
            throw new NotFoundException("asset with id: " + entity.getId() + " not found");
        }
        return new InputStreamResource(new ByteArrayInputStream(assetFileEntity.getBinary()));
    }

    @SneakyThrows
    @Override
    public InputStreamResource downloadPreview(AssetReference reference, PreviewSize previewSize) {
        AssetFileEntity assetFileEntity = entityManager.find(AssetFileEntity.class, buildPreviewSizeId(reference, previewSize));
        if (assetFileEntity == null) {
            throw new NotFoundException("asset with id: " + reference.getId() + " not found");
        }
        return new InputStreamResource(new ByteArrayInputStream(assetFileEntity.getBinary()));
    }

    @Override
    public void delete(AssetEntity entity) {
        AssetFileEntity assetFileEntity = entityManager.find(AssetFileEntity.class, entity.getId());
        if (assetFileEntity == null) {
            throw new NotFoundException("asset with id: " + entity.getId() + " not found");
        }
        entityManager.remove(assetFileEntity);

        if (assetApiProperties.isPrecalculate()) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaDelete<AssetFileEntity> criteriaDelete = cb.createCriteriaDelete(AssetFileEntity.class);
            Root<AssetFileEntity> root = criteriaDelete.from(AssetFileEntity.class);
            criteriaDelete.where(cb.like(root.get("id"), entity.getId() + "-%"));
            entityManager.createQuery(criteriaDelete).executeUpdate();
        }
    }

    @SneakyThrows
    @Override
    public void copy(AssetEntity source, AssetEntity target) {
        InputStreamResource download = download(source);
        File tempFile = File.createTempFile("asset-download", source.getType().getFileExtensionForSuffix());
        IOUtils.copy(download.getInputStream(), new FileOutputStream(tempFile));

        upload(target, tempFile);
    }
}
