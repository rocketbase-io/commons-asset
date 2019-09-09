package io.rocketbase.commons.service;

import io.rocketbase.commons.model.AssetEntity;
import org.springframework.core.io.InputStreamResource;

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
     * @return tempfile that has content of storage
     */
    InputStreamResource download(AssetEntity entity);

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
