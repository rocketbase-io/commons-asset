package io.rocketbase.commons.service;

import com.mongodb.client.result.DeleteResult;
import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.model.AssetMongoEntity;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class AssetMongoRepository implements AssetRepository<AssetMongoEntity> {

    private final MongoTemplate mongoTemplate;

    /**
     * search first by id, when not found by systemRefId
     *
     * @param sid database id or systemRefId
     */
    @Override
    public Optional<AssetMongoEntity> findByIdOrSystemRefId(String sid) {
        Optional<AssetMongoEntity> optional = findById(sid);
        if (!optional.isPresent()) {
            return findBySystemRefId(sid);
        }
        return optional;
    }

    @Override
    public Optional<AssetMongoEntity> findById(String sid) {
        AssetMongoEntity entity = mongoTemplate.findOne(getIdQuery(sid), AssetMongoEntity.class);
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<AssetMongoEntity> findBySystemRefId(String systemRefId) {
        AssetMongoEntity entity = mongoTemplate.findOne(new Query(Criteria.where("systemRefId")
                .is(systemRefId)), AssetMongoEntity.class);
        return Optional.ofNullable(entity);
    }

    @Override
    public boolean delete(String id) {
        DeleteResult deleteResult = mongoTemplate.remove(getIdQuery(id), AssetMongoEntity.class);
        return deleteResult.getDeletedCount() > 0;
    }

    @Override
    public void save(AssetMongoEntity entity) {
        mongoTemplate.save(entity);
    }

    @Override
    public Page<AssetMongoEntity> findAll(QueryAsset query, Pageable pageable) {
        List<AssetMongoEntity> assetEntities = mongoTemplate.find(getQuery(query).with(pageable), AssetMongoEntity.class);
        long totalCount = mongoTemplate.count(getQuery(query), AssetMongoEntity.class);

        return new PageImpl<>(assetEntities, pageable, totalCount);
    }

    @Override
    public AssetMongoEntity initNewInstance() {
        return AssetMongoEntity.builder()
                .id(ObjectId.get().toHexString())
                .created(LocalDateTime.now())
                .build();
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
