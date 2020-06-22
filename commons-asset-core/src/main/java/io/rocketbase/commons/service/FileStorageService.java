package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetReferenceType;
import io.rocketbase.commons.model.AssetEntity;
import org.springframework.core.io.InputStreamResource;

import javax.annotation.Nullable;
import java.io.File;

public interface FileStorageService {

    /**
     * upload resource to storage and updates urlPath withn entity
     *
     * @param entity predefined entity that should get stored afterwards
     * @param file   source
     */
    void upload(AssetEntity entity, File file);

    /**
     * download resource to tempfile
     *
     * @param entity of database
     * @return temp-file that has content of storage
     */
    InputStreamResource download(AssetEntity entity);

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
     * delete given id from storage
     *
     * @param entity of database
     */
    void delete(AssetEntity entity);

    default boolean localEndpoint() {
        return true;
    }

}
