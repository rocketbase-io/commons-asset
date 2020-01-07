package io.rocketbase.commons.repository;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.model.AssetJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AssetEntityRepository extends PagingAndSortingRepository<AssetJpaEntity, String> {
    Optional<AssetJpaEntity> findBySystemRefId(String systemRefId);

    @Query("select a from AssetJpaEntity a where a.created between :before and :after and "
            + "(:originalFilename is null or lower(a.originalFilename) like %:originalFilename%) and "
            + "(:referenceHash is null or a.referenceHash = :referenceHash) and "
            + "(:context is null or lower(a.context) like %:context%) and "
            + "a.type in (:types)")
    Page<AssetJpaEntity> findAllWithDates(@Param("before") Instant before, @Param("after") Instant after,
                                          @Param("originalFilename") String originalFilename, @Param("referenceHash") String referenceHash,
                                          @Param("context") String context, @Param("types") List<AssetType> types, Pageable pageable);

    @Query("select a from AssetJpaEntity a where "
            + "(:originalFilename is null or lower(a.originalFilename) like %:originalFilename%) and "
            + "(:referenceHash is null or a.referenceHash = :referenceHash) and "
            + "(:context is null or lower(a.context) like %:context%) and "
            + "a.type in (:types)")
    Page<AssetJpaEntity> findAllBy(@Param("originalFilename") String originalFilename, @Param("referenceHash") String referenceHash,
                                   @Param("context") String context, @Param("types") List<AssetType> types, Pageable pageable);

    @Query("select a from AssetJpaEntity a where a.referenceHash is null and a.referenceUrl is not null")
    Page<AssetJpaEntity> findAssetsWithMissingReferenceHash(Pageable pageable);

}
