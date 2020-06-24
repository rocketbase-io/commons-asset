package io.rocketbase.commons.service;

import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.model.AssetFileEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.core.io.InputStreamResource;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@RequiredArgsConstructor
public class JpaFileStorageService implements FileStorageService {

    private final EntityManager entityManager;

    @SneakyThrows
    @Override
    @Transactional
    public void upload(AssetEntity entity, File file) {
        AssetFileEntity assetFileEntity = AssetFileEntity.builder()
                .id(entity.getId())
                .binary(BlobProxy.generateProxy(new FileInputStream(file), file.length()))
                .build();
        entityManager.persist(assetFileEntity);
        entity.setUrlPath(entity.getId());
    }

    @SneakyThrows
    @Override
    @Transactional
    public InputStreamResource download(AssetEntity entity) {
        AssetFileEntity assetFileEntity = entityManager.find(AssetFileEntity.class, entity.getId());
        return new InputStreamResource(assetFileEntity.getBinary().getBinaryStream());
    }

    @Override
    @Transactional
    public void delete(AssetEntity entity) {
        AssetFileEntity assetFileEntity = entityManager.find(AssetFileEntity.class, entity.getId());
        entityManager.remove(assetFileEntity);
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
