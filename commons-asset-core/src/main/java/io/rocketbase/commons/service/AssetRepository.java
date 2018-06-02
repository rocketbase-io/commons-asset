package io.rocketbase.commons.service;

import com.mongodb.client.result.DeleteResult;
import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AssetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    public Page<AssetEntity> findAll(QueryAsset query, Pageable pageable) {
        List<AssetEntity> assetEntities = mongoTemplate.find(getQuery(query).with(pageable), AssetEntity.class);
        long totalCount = mongoTemplate.count(getQuery(query), AssetEntity.class);

        return new PageImpl<AssetEntity>(assetEntities, pageable, totalCount);
    }

    private Query getIdQuery(String id) {
        return new Query(Criteria.where("_id").is(id));
    }

    private Query getQuery(QueryAsset query) {
        Query result = new Query();
        if (query != null) {
            if (query.getBefore() != null || query.getAfter() != null) {
                Criteria criteria = Criteria.where("created");
                if (query.getBefore() != null) {
                    criteria.lte(query.getBefore());
                }
                if (query.getAfter() != null) {
                    criteria.gte(query.getAfter());
                }
                result.addCriteria(criteria);
            }
            if (query.getOriginalFilename() != null) {
                result.addCriteria(Criteria.where("originalFilename").regex(query.getOriginalFilename(), "i"));
            }
            if (query.getReferenceUrl() != null) {
                result.addCriteria(Criteria.where("referenceUrl").regex(query.getReferenceUrl(), "i"));
            }
            if (query.getTypes() != null && !query.getTypes().isEmpty()) {
                result.addCriteria(Criteria.where("type").in(query.getTypes()));
            }
        }
        return result;
    }
}
