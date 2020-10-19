package io.rocketbase.commons.service;

import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.dto.asset.AssetReference;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.util.UrlParts;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.InputStreamResource;

import java.io.File;
import java.io.FileInputStream;

public class FsFileStorageService implements FileStorageService {

    private final String basePath;
    private final PathResolver pathResolver;
    private final AssetApiProperties assetApiProperties;


    public FsFileStorageService(String basePath, PathResolver pathResolver, AssetApiProperties assetApiProperties) {
        this.basePath = UrlParts.ensureEndsWithSlash(basePath);
        this.pathResolver = pathResolver;
        this.assetApiProperties = assetApiProperties;
    }

    @SneakyThrows
    @Override
    public void upload(AssetEntity entity, File file) {
        String filePath = pathResolver.getAbsolutePath(entity);
        FileUtils.copyFile(file, new File(basePath + filePath));
        entity.setUrlPath(filePath);
    }

    @SneakyThrows
    @Override
    public void storePreview(AssetReference reference, File file, PreviewSize previewSize) {
        String filePath = pathResolver.getAbsolutePath(reference);
        FileUtils.copyFile(file, getPreviewFile(previewSize, reference));
    }

    @SneakyThrows
    @Override
    public InputStreamResource download(AssetEntity entity) {
        return new InputStreamResource(new FileInputStream(new File(basePath + pathResolver.getAbsolutePath(entity))));
    }

    @SneakyThrows
    @Override
    public InputStreamResource downloadPreview(AssetReference reference, PreviewSize previewSize) {
        return new InputStreamResource(new FileInputStream(getPreviewFile(previewSize, reference)));
    }

    protected File getPreviewFile(PreviewSize previewSize, AssetReference reference) {
        return new File(basePath + previewSize.getPreviewStoragePath() + "/" + pathResolver.getAbsolutePath(reference));
    }

    @Override
    public void delete(AssetEntity entity) {
        new File(basePath + pathResolver.getAbsolutePath(entity)).delete();
        if (assetApiProperties.isPrecalculate()) {
            for (PreviewSize size : PreviewSize.values()) {
                File previewFile = getPreviewFile(size, entity);
                if (previewFile.exists()) {
                    previewFile.delete();
                }
            }
        }
    }

    @SneakyThrows
    @Override
    public void copy(AssetEntity source, AssetEntity target) {
        String filePath = pathResolver.getAbsolutePath(target);
        FileUtils.copyFile(new File(basePath + pathResolver.getAbsolutePath(source)), new File(basePath + filePath));
        target.setUrlPath(filePath);
    }
}
