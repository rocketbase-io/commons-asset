package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.model.AssetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AssetRepository<T extends AssetEntity> {

    Optional<T> findById(String id);

    boolean delete(String id);

    T save(T entity);

    Page<T> findAll(QueryAsset query, Pageable pageable);

    T initNewInstance();

}
