package io.rocketbase.commons.service;

import com.google.common.hash.Hashing;
import io.rocketbase.commons.model.AssetJpaEntity;
import io.rocketbase.commons.repository.AssetEntityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.nio.charset.Charset;

public class ReferenceHashMigrationService {

    @Resource
    private AssetEntityRepository assetEntityRepository;

    @Transactional
    public void generateHashesForReferenceUrls(int batchSize) {
        boolean updated;
        do {
            updated = updateBatch(batchSize);
        } while (updated);
    }

    private boolean updateBatch(int batchSize) {
        Page<AssetJpaEntity> page = assetEntityRepository.findAssetsWithMissingReferenceHash(PageRequest.of(0, batchSize));
        if (page.getNumberOfElements() > 0) {
            for (AssetJpaEntity e : page.getContent()) {
                e.setReferenceHash(Hashing.sha256().hashString(e.getReferenceUrl(), Charset.forName("UTF8")).toString());
            }
            assetEntityRepository.saveAll(page.getContent());
            return true;
        }
        return false;
    }

}
