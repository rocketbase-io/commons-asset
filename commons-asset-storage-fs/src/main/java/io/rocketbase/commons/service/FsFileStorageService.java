package io.rocketbase.commons.service;

import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.util.UrlParts;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.InputStreamResource;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;

public class FsFileStorageService implements FileStorageService {

    private final String basePath;
    private final PathResolver pathResolver;


    public FsFileStorageService(String basePath, PathResolver pathResolver) {
        this.basePath = UrlParts.ensureEndsWithSlash(basePath);
        this.pathResolver = pathResolver;
    }

    @SneakyThrows
    @Override
    @Transactional
    public void upload(AssetEntity entity, File file) {
        String filePath = getFilePath(entity);
        FileUtils.copyFile(file, new File(basePath + filePath));
        entity.setUrlPath(filePath);
    }

    protected String getFilePath(AssetEntity entity) {
        return String.format("%s%s.%s", UrlParts.ensureEndsWithSlash(pathResolver.resolvePath(entity)), entity.getId(), entity.getType().getFileExtension())
                .toLowerCase();
    }

    @SneakyThrows
    @Override
    @Transactional
    public InputStreamResource download(AssetEntity entity) {
        return new InputStreamResource(new FileInputStream(new File(basePath + getFilePath(entity))));
    }

    @Override
    @Transactional
    public void delete(AssetEntity entity) {
        new File(basePath + getFilePath(entity)).delete();
    }
}
