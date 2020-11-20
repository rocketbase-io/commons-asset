package io.rocketbase.commons.service;

import com.google.common.hash.Hashing;
import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.model.AssetJpaEntity;
import io.rocketbase.commons.model.AssetJpaKeyValueJpaEntity;
import io.rocketbase.commons.repository.AssetEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
public class AssetJpaRepository implements AssetRepository<AssetJpaEntity>, PredicateHelper {

    private final AssetEntityRepository assetEntityRepository;

    public static String hashValue(String value) {
        if (value == null) {
            return null;
        }
        return Hashing.sha256().hashString(value, Charset.forName("UTF8")).toString();
    }

    /**
     * search first by id, when not found by systemRefId
     *
     * @param sid database id or systemRefId
     */
    @Override
    public Optional<AssetJpaEntity> findByIdOrSystemRefId(String sid) {
        Optional<AssetJpaEntity> optional = findById(sid);
        if (!optional.isPresent()) {
            return findBySystemRefId(sid);
        }
        return optional;
    }

    @Override
    public Optional<AssetJpaEntity> findById(String sid) {
        return assetEntityRepository.findById(sid);
    }

    @Override
    public Optional<AssetJpaEntity> findBySystemRefId(String systemRefId) {
        return assetEntityRepository.findBySystemRefId(systemRefId);
    }

    @Override
    @Transactional
    public boolean delete(String id) {
        Optional<AssetJpaEntity> optional = findById(id);
        assetEntityRepository.deleteById(id);
        return optional.isPresent();
    }

    @Override
    @Transactional
    public AssetJpaEntity save(AssetJpaEntity entity) {
        return initLazyObjects(assetEntityRepository.save(entity));
    }

    @Override
    @Transactional
    public Page<AssetJpaEntity> findAll(QueryAsset query, Pageable pageable) {
        if (query == null) {
            return assetEntityRepository.findAll(pageable);
        }

        Specification<AssetJpaEntity> specification = (Specification<AssetJpaEntity>) (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query.getBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("created"), query.getBefore()));
            }
            if (query.getAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("created"), query.getAfter()));
            }

            addToListIfNotEmpty(predicates, query.getOriginalFilename(), "originalFilename", root, cb);

            if (query.getReferenceUrl() != null) {
                String referenceHash = Hashing.sha256().hashString(query.getReferenceUrl(), Charset.forName("UTF8")).toString();
                predicates.add(cb.equal(root.get("referenceHash"), referenceHash));
            }
            addToListIfNotEmpty(predicates, query.getContext(), "context", root, cb);

            if (query.getTypes() != null && !query.getTypes().isEmpty()) {
                predicates.add(cb.in(root.get("type")).value(query.getTypes()));
            }
            if (query.getHasEolValue() != null) {
                if (query.getHasEolValue()) {
                    predicates.add(cb.isNotNull(root.get("eol")));
                } else {
                    predicates.add(cb.isNull(root.get("eol")));
                }
            }

            if (query.getIsEol() != null) {
                if (query.getIsEol()) {
                    predicates.add(cb.and(cb.isNotNull(root.get("eol")), cb.lessThan(root.get("eol"), Instant.now())));
                } else {
                    predicates.add(cb.or(cb.isNull(root.get("eol")), cb.greaterThanOrEqualTo(root.get("eol"), Instant.now())));
                }
            }

            criteriaQuery.distinct(true);
            root.fetch("keyValues", JoinType.LEFT);

            if (query.getKeyValues() != null && !query.getKeyValues().isEmpty()) {
                Subquery<AssetJpaKeyValueJpaEntity> subQuery = criteriaQuery.subquery(AssetJpaKeyValueJpaEntity.class);
                Root<AssetJpaKeyValueJpaEntity> subRoot = subQuery.from(AssetJpaKeyValueJpaEntity.class);
                Join<AssetJpaKeyValueJpaEntity, AssetJpaEntity> subJoin = subRoot.join("asset", JoinType.INNER);
                subQuery.select(subJoin.get("id"));

                List<Predicate> subPredicates = new ArrayList<>();
                for (Map.Entry<String, String> keyEntry : query.getKeyValues().entrySet()) {
                    subPredicates.add(cb.and(cb.equal(subRoot.get("fieldKey"), keyEntry.getKey()),
                            cb.equal(subRoot.get("fieldValueHash"), hashValue(keyEntry.getValue().toLowerCase()))
                    ));
                }
                subQuery.where(subPredicates.toArray(new Predicate[]{}));
                Expression<String> exp = root.get("id");
                predicates.add(exp.in(subQuery));
            }
            return cb.and(predicates.toArray(new Predicate[]{}));
        };
        return assetEntityRepository.findAll(specification, pageable);
    }

    @Override
    public AssetJpaEntity initNewInstance() {
        return AssetJpaEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(Instant.now())
                .build();
    }

    protected AssetJpaEntity initLazyObjects(AssetJpaEntity entity) {
        if (entity != null && entity.getKeyValues() != null) {
            // in order to initialize lazy map
            entity.getKeyValues().size();
        }
        return entity;
    }

}
