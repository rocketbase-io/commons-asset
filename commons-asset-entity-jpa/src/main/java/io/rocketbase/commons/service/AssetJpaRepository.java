package io.rocketbase.commons.service;

import com.google.common.hash.Hashing;
import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.model.AssetJpaEntity;
import io.rocketbase.commons.repository.AssetEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
public class AssetJpaRepository implements AssetRepository<AssetJpaEntity>, PredicateHelper {

    private final AssetEntityRepository assetEntityRepository;

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
        if (!StringUtils.isEmpty(entity.getReferenceUrl())) {
            entity.setReferenceHash(Hashing.sha256().hashString(entity.getReferenceUrl(), Charset.forName("UTF8")).toString());
        }
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

            if (query.getKeyValues() != null && !query.getKeyValues().isEmpty()) {
                criteriaQuery.distinct(true);
                MapJoin<AssetJpaEntity, String, String> mapJoin = root.joinMap("keyValueMap");
                for (Map.Entry<String, String> keyEntry : query.getKeyValues().entrySet()) {
                    predicates.add(cb.and(cb.equal(mapJoin.key(), keyEntry.getKey()), cb.equal(cb.lower(mapJoin.value()), keyEntry.getValue().toLowerCase())));
                }
            }
            return cb.and(predicates.toArray(new Predicate[]{}));
        };

        Page<AssetJpaEntity> result = assetEntityRepository.findAll(specification, pageable);
        // in order to initialize lazy map
        result.stream()
                .forEach(v -> initLazyObjects(v));
        return result;
    }

    @Override
    public AssetJpaEntity initNewInstance() {
        return AssetJpaEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(Instant.now())
                .build();
    }

    protected AssetJpaEntity initLazyObjects(AssetJpaEntity entity) {
        if (entity != null && entity.getKeyValueMap() != null) {
            // in order to initialize lazy map
            entity.getKeyValueMap().size();
        }
        return entity;
    }

}
