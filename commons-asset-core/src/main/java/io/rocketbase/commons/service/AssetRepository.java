package io.rocketbase.commons.service;

import com.mongodb.client.result.DeleteResult;
import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.model.AssetEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class AssetRepository {

    private final MongoTemplate mongoTemplate;

    /**
     * search first by id, when not found by systemRefId
     *
     * @param sid database id or systemRefId
     */
    public Optional<AssetEntity> findByIdOrSystemRefId(String sid) {
        Optional<AssetEntity> optional = findById(sid);
        if (!optional.isPresent()) {
            return findBySystemRefId(sid);
        }
        return optional;
    }

    public Optional<AssetEntity> findById(String sid) {
        AssetEntity entity = mongoTemplate.findOne(getIdQuery(sid), AssetEntity.class);
        return Optional.ofNullable(entity);
    }

    public Optional<AssetEntity> findBySystemRefId(String systemRefId) {
        AssetEntity entity = mongoTemplate.findOne(new Query(Criteria.where("systemRefId")
                .is(systemRefId)), AssetEntity.class);
        return Optional.ofNullable(entity);
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

        return new PageImpl<>(assetEntities, pageable, totalCount);
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
            if (!StringUtils.isEmpty(query.getOriginalFilename())) {
                result.addCriteria(Criteria.where("originalFilename").regex(query.getOriginalFilename().trim(), "i"));
            }
            if (!StringUtils.isEmpty(query.getReferenceUrl())) {
                result.addCriteria(Criteria.where("referenceUrl").regex(query.getReferenceUrl().trim(), "i"));
            }
            if (!StringUtils.isEmpty(query.getContext())) {
                result.addCriteria(Criteria.where("context").is(query.getContext()));
            }
            if (query.getTypes() != null && !query.getTypes().isEmpty()) {
                result.addCriteria(Criteria.where("type").in(query.getTypes()));
            }
        }
        return result;
    }
}
