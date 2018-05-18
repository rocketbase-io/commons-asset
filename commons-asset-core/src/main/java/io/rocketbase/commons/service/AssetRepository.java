package io.rocketbase.commons.service;

import com.mongodb.client.result.DeleteResult;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AssetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AssetRepository {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * search first by id, when not found by systemRefId
     *
     * @param sid databsae id or systemRefId
     * @return will throw NotFoundException when not found
     */
    public AssetEntity getByIdOrSystemRefId(String sid) {
        AssetEntity entity = mongoTemplate.findOne(getIdQuery(sid), AssetEntity.class);
        if (entity == null) {
            entity = findBySystemRefId(sid);
            if (entity == null) {
                throw new NotFoundException();
            }
        }
        return entity;
    }

    public AssetEntity findBySystemRefId(String systemRefId) {
        return mongoTemplate.findOne(new Query(Criteria.where("systemRefId")
                .is(systemRefId)), AssetEntity.class);
    }

    public boolean delete(String id) {
        DeleteResult deleteResult = mongoTemplate.remove(getIdQuery(id), AssetEntity.class);
        return deleteResult.getDeletedCount() > 0;
    }

    public void save(AssetEntity entity) {
        mongoTemplate.save(entity);
    }

    public Page<AssetEntity> findAll(PageRequest pageable) {
        List<AssetEntity> assetEntities = mongoTemplate.find(new Query().with(pageable), AssetEntity.class);
        long totalCount = mongoTemplate.count(new Query(), AssetEntity.class);

        return new PageImpl<AssetEntity>(assetEntities, pageable, totalCount);
    }

    private Query getIdQuery(String id) {
        return new Query(Criteria.where("_id").is(id));
    }
}
