package io.rocketbase.commons.service;

import com.google.common.hash.Hashing;
import io.rocketbase.commons.model.AssetJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.nio.charset.Charset;

public class ReferenceHashMigrationService {

    private final EntityManager em;
    private final SimpleJpaRepository<AssetJpaEntity, String> repository;

    public ReferenceHashMigrationService(EntityManager entityManager) {
        this.em = entityManager;
        repository = new SimpleJpaRepository<>(AssetJpaEntity.class, entityManager);
    }

    @Transactional
    public void generateHashesForReferenceUrls(int batchSize) {
        boolean updated;
        do {
            updated = updateBatch(batchSize);
        } while (updated);
    }

    private boolean updateBatch(int batchSize) {
        Specification<AssetJpaEntity> specification = (root, criteriaQuery, cb) ->
                cb.and(cb.isNull(root.get("referenceHash")), cb.isNotNull(root.get("referenceUrl")));

        Page<AssetJpaEntity> page = repository.findAll(specification, PageRequest.of(0, batchSize));
        if (page.getNumberOfElements() > 0) {
            for (AssetJpaEntity e : page.getContent()) {
                e.setReferenceHash(Hashing.sha256().hashString(e.getReferenceUrl(), Charset.forName("UTF8")).toString());
            }
            repository.saveAll(page.getContent());
            return true;
        }
        return false;
    }

}
