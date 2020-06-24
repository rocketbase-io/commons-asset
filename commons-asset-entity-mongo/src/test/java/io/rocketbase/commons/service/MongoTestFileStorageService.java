package io.rocketbase.commons.service;

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
    public InputStreamResource download(AssetEntity entity) {
        return null;
    }

    @Override
    public void delete(AssetEntity entity) {
    }

    @Override
    public void copy(AssetEntity source, AssetEntity target) {

    }
}
