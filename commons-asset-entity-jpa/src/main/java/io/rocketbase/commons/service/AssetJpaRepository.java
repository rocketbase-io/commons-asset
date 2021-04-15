package io.rocketbase.commons.service;

import com.google.common.hash.Hashing;
import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.model.AssetJpaEntity;
import io.rocketbase.commons.model.AssetJpaEntity_;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
public class AssetJpaRepository implements AssetRepository<AssetJpaEntity>, PredicateHelper {

    private final EntityManager em;
    private final AuditorAware auditorAware;
    private final SimpleJpaRepository<AssetJpaEntity, String> repository;

    public AssetJpaRepository(EntityManager entityManager, AuditorAware auditorAware) {
        this.em = entityManager;
        this.auditorAware = auditorAware;
        repository = new SimpleJpaRepository<>(AssetJpaEntity.class, entityManager);
    }

    public static String hashValue(String value) {
        if (value == null) {
            return null;
        }
        return Hashing.sha256().hashString(value, Charset.forName("UTF8")).toString();
    }


    @Override
    public Optional<AssetJpaEntity> findById(String id) {
        Specification<AssetJpaEntity> specification = (root, criteriaQuery, cb) -> {
            root.fetch(AssetJpaEntity_.KEY_VALUE_MAP, JoinType.LEFT);
            return cb.and(cb.equal(root.get(AssetJpaEntity_.ID), id));
        };
        return repository.findOne(specification);
    }

    @Override
    @Transactional
    public boolean delete(String id) {
        Optional<AssetJpaEntity> optional = findById(id);
        repository.deleteById(id);
        return optional.isPresent();
    }

    @Override
    @Transactional
    public AssetJpaEntity save(AssetJpaEntity entity) {
        entity.setModified(entity.getModified() == null ? entity.getCreated() : Instant.now());
        entity.setModifiedBy(String.valueOf(auditorAware.getCurrentAuditor().orElse("")));
        return initLazyObjects(repository.save(entity));
    }

    @Override
    @Transactional
    public Page<AssetJpaEntity> findAll(QueryAsset query, Pageable pageable) {
        if (query == null) {
            return repository.findAll(pageable);
        }

        Specification<AssetJpaEntity> specification = (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query.getBefore() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(AssetJpaEntity_.CREATED), query.getBefore()));
            }
            if (query.getAfter() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(AssetJpaEntity_.CREATED), query.getAfter()));
            }

            addToListIfNotEmpty(predicates, query.getOriginalFilename(), AssetJpaEntity_.ORIGINAL_FILENAME, root, cb);

            if (StringUtils.hasText(query.getContext())) {
                predicates.add(cb.equal(root.get(AssetJpaEntity_.SYSTEM_REF_ID), query.getSystemRefId()));
            }

            if (query.getReferenceUrl() != null) {
                String referenceHash = Hashing.sha256().hashString(query.getReferenceUrl(), Charset.forName("UTF8")).toString();
                predicates.add(cb.equal(root.get(AssetJpaEntity_.REFERENCE_HASH), referenceHash));
            }
            if (StringUtils.hasText(query.getContext())) {
                predicates.add(cb.equal(root.get(AssetJpaEntity_.CONTEXT), query.getContext()));
            }

            if (query.getTypes() != null && !query.getTypes().isEmpty()) {
                predicates.add(cb.in(root.get(AssetJpaEntity_.TYPE)).value(query.getTypes()));
            }
            if (query.getHasEolValue() != null) {
                if (query.getHasEolValue()) {
                    predicates.add(cb.isNotNull(root.get(AssetJpaEntity_.EOL)));
                } else {
                    predicates.add(cb.isNull(root.get(AssetJpaEntity_.EOL)));
                }
            }

            if (query.getIsEol() != null) {
                if (query.getIsEol()) {
                    predicates.add(cb.and(cb.isNotNull(root.get(AssetJpaEntity_.EOL)), cb.lessThan(root.get(AssetJpaEntity_.EOL), Instant.now())));
                } else {
                    predicates.add(cb.or(cb.isNull(root.get(AssetJpaEntity_.EOL)), cb.greaterThanOrEqualTo(root.get(AssetJpaEntity_.EOL), Instant.now())));
                }
            }

            criteriaQuery.distinct(true);
            root.fetch(AssetJpaEntity_.KEY_VALUE_MAP, JoinType.LEFT);

            if (query.getKeyValues() != null && !query.getKeyValues().isEmpty()) {
                criteriaQuery.distinct(true);
                MapJoin<AssetJpaEntity, String, String> mapJoin = root.joinMap(AssetJpaEntity_.KEY_VALUE_MAP);
                for (Map.Entry<String, String> keyEntry : query.getKeyValues().entrySet()) {
                    predicates.add(cb.and(cb.equal(mapJoin.key(), keyEntry.getKey()), cb.equal(cb.lower(mapJoin.value()), keyEntry.getValue().toLowerCase())));
                }
            }
            return cb.and(predicates.toArray(new Predicate[]{}));
        };
        return repository.findAll(specification, pageable);
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
