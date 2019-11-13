package io.rocketbase.commons.service;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.model.AssetJpaEntity;
import io.rocketbase.commons.repository.AssetEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class AssetJpaRepository implements AssetRepository<AssetJpaEntity> {

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
        return assetEntityRepository.save(entity);
    }

    @Override
    public Page<AssetJpaEntity> findAll(QueryAsset query, Pageable pageable) {
        if (query != null && (query.getBefore() != null || query.getAfter() != null)) {
            return assetEntityRepository.findAllWithDates(query.getBefore(), query.getAfter(),
                    notEmptyToLowercase(query.getOriginalFilename()), notEmptyToLowercase(query.getReferenceUrl()),
                    notEmptyToLowercase(query.getContext()), typesFilter(query), pageable);
        } else if (query != null) {
            return assetEntityRepository.findAllBy(notEmptyToLowercase(query.getOriginalFilename()), notEmptyToLowercase(query.getReferenceUrl()),
                    notEmptyToLowercase(query.getContext()), typesFilter(query), pageable);
        } else {
            return assetEntityRepository.findAll(pageable);
        }
    }

    private String notEmptyToLowercase(String property) {
        if (StringUtils.isEmpty(property)) {
            return null;
        }
        return property.trim().toLowerCase();
    }

    private List<AssetType> typesFilter(QueryAsset query) {
        if (query != null && !query.getTypes().isEmpty()) {
            return query.getTypes();
        }
        return Arrays.asList(AssetType.values());
    }

    @Override
    public AssetJpaEntity initNewInstance() {
        return AssetJpaEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(Instant.now())
                .build();
    }

}
