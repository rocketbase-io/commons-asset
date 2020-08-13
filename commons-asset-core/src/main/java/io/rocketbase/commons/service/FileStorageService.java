package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetReferenceType;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.model.AssetEntity;
import org.springframework.core.io.InputStreamResource;

import javax.annotation.Nullable;
import java.io.File;

public interface FileStorageService {

    /**
     * upload resource to storage and updates urlPath within entity
     *
     * @param entity predefined entity that should get stored afterwards
     * @param file   source
     */
    void upload(AssetEntity entity, File file);

    /**
     * stores a preview of assetEntity
     *
     * @param reference   asset reference
     * @param file        preview source
     * @param previewSize size of file
     */
    void storePreview(AssetReferenceType reference, File file, PreviewSize previewSize);

    /**
     * download resource to tempfile
     *
     * @param entity of database
     * @return temp-file that has content of storage
     */
    InputStreamResource download(AssetEntity entity);

    /**
     * download resource to tempfile
     *
     * @param reference   asset reference
     * @param previewSize size of preview
     * @return temp-file that has content of storage
     */
    InputStreamResource downloadPreview(AssetReferenceType reference, PreviewSize previewSize);

    /**
     * fetch download url
     *
     * @param reference asset reference
     * @return download url that may could expire after some days<br>
     * could also return null when not implemented
     */
    @Nullable
    default String getDownloadUrl(AssetReferenceType reference) {
        return null;
    }


    /**
     * fetch download preview url
     *
     * @param reference   asset reference
     * @param previewSize size of preview
     * @return download url that may could expire after some days<br>
     * could also return null when not implemented
     */
    @Nullable
    default String getDownloadPreviewUrl(AssetReferenceType reference, PreviewSize previewSize) {
        return null;
    }

    /**
     * delete given id from storage
     *
     * @param entity of database
     */
    void delete(AssetEntity entity);

    /**
     * physically copy file
     *
     * @param source of database
     * @param target new instance with it's new id
     */
    void copy(AssetEntity source, AssetEntity target);

    default boolean localEndpoint() {
        return true;
    }

    default boolean useDownloadPreviewUrl() {
        return false;
    }

}
