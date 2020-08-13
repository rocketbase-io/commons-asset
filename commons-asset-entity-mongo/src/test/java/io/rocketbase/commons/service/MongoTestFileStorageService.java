package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetReferenceType;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.model.AssetEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class MongoTestFileStorageService implements FileStorageService {

    @Override
    public void upload(AssetEntity entity, File file) {
    }

    @Override
    public void storePreview(AssetReferenceType reference, File file, PreviewSize previewSize) {

    }

    @Override
    public InputStreamResource download(AssetEntity entity) {
        return null;
    }

    @Override
    public InputStreamResource downloadPreview(AssetReferenceType reference, PreviewSize previewSize) {
        return null;
    }

    @Override
    public void delete(AssetEntity entity) {
    }

    @Override
    public void copy(AssetEntity source, AssetEntity target) {

    }
}
