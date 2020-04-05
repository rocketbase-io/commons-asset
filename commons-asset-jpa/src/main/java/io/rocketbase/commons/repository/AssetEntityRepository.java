package io.rocketbase.commons.repository;

import io.rocketbase.commons.model.AssetJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface AssetEntityRepository extends PagingAndSortingRepository<AssetJpaEntity, String>, JpaSpecificationExecutor<AssetJpaEntity> {
    Optional<AssetJpaEntity> findBySystemRefId(String systemRefId);

    @Query("select a from AssetJpaEntity a where a.referenceHash is null and a.referenceUrl is not null")
    Page<AssetJpaEntity> findAssetsWithMissingReferenceHash(Pageable pageable);

}
