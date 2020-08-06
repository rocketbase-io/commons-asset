package io.rocketbase.commons.repository;

import io.rocketbase.commons.model.AssetJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AssetEntityRepository extends PagingAndSortingRepository<AssetJpaEntity, String>, JpaSpecificationExecutor<AssetJpaEntity> {

    @Query("select a from AssetJpaEntity a left join fetch a.keyValueMap where a.id = :id")
    Optional<AssetJpaEntity> findById(@Param("id") String id);

    @Query("select a from AssetJpaEntity a left join fetch a.keyValueMap where a.systemRefId = :systemRefId")
    Optional<AssetJpaEntity> findBySystemRefId(@Param("systemRefId") String systemRefId);

    @Query("select a from AssetJpaEntity a where a.referenceHash is null and a.referenceUrl is not null")
    Page<AssetJpaEntity> findAssetsWithMissingReferenceHash(Pageable pageable);

    @Query(value = "select a from AssetJpaEntity a left join fetch a.keyValueMap",
            countQuery = "select count(a) from AssetJpaEntity a")
    Page<AssetJpaEntity> findAll(Pageable pageable);

}
