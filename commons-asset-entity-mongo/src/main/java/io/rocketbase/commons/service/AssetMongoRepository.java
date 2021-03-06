package io.rocketbase.commons.service;

import com.mongodb.client.result.DeleteResult;
import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.model.AssetMongoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class AssetMongoRepository implements AssetRepository<AssetMongoEntity> {

    private final MongoTemplate mongoTemplate;
    private final MongoMappingContext mongoMappingContext;
    private final boolean mongoEnsureIndex;
    private final AuditorAware auditorAware;

    @Override
    public Optional<AssetMongoEntity> findById(String sid) {
        AssetMongoEntity entity = mongoTemplate.findOne(getIdQuery(sid), AssetMongoEntity.class);
        return Optional.ofNullable(entity);
    }

    @Override
    public boolean delete(String id) {
        DeleteResult deleteResult = mongoTemplate.remove(getIdQuery(id), AssetMongoEntity.class);
        return deleteResult.getDeletedCount() > 0;
    }

    @Override
    public AssetMongoEntity save(AssetMongoEntity entity) {
        entity.setModified(entity.getModified() == null ? entity.getCreated() : Instant.now());
        entity.setModifiedBy(String.valueOf(auditorAware.getCurrentAuditor().orElse("")));
        return mongoTemplate.save(entity);
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
                .created(Instant.now())
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
            if (StringUtils.hasText(query.getSystemRefId())) {
                result.addCriteria(Criteria.where("systemRefId").is(query.getSystemRefId()));
            }
            if (StringUtils.hasText(query.getOriginalFilename())) {
                result.addCriteria(Criteria.where("originalFilename").regex(query.getOriginalFilename().trim(), "i"));
            }
            if (StringUtils.hasText(query.getReferenceUrl())) {
                result.addCriteria(Criteria.where("referenceUrl").regex(query.getReferenceUrl().trim(), "i"));
            }
            if (StringUtils.hasText(query.getContext())) {
                result.addCriteria(Criteria.where("context").is(query.getContext()));
            }
            if (query.getTypes() != null && !query.getTypes().isEmpty()) {
                result.addCriteria(Criteria.where("type").in(query.getTypes()));
            }
            if (query.getHasEolValue() != null) {
                result.addCriteria(Criteria.where("eol").exists(query.getHasEolValue()));
            }
            if (query.getIsEol() != null) {
                if (query.getIsEol()) {
                    result.addCriteria(Criteria.where("eol").exists(true)
                            .andOperator(Criteria.where("eol").lt(Instant.now())));
                } else {
                    result.addCriteria(Criteria.where("eol").exists(false)
                            .orOperator(Criteria.where("eol").gte(Instant.now())));
                }
            }
            if (query.getKeyValues() != null && !query.getKeyValues().isEmpty()) {
                for (Map.Entry<String, String> kv : query.getKeyValues().entrySet()) {
                    Pattern valuePattern = Pattern.compile(kv.getValue(), Pattern.CASE_INSENSITIVE);
                    result.addCriteria(Criteria.where("keyValueMap." + kv.getKey()).is(valuePattern));
                }
            }
        }
        return result;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initIndicesAfterStartup() {
        if (mongoEnsureIndex) {
            IndexOperations indexOperations = mongoTemplate.indexOps(AssetMongoEntity.class);
            for (IndexInfo i : indexOperations.getIndexInfo()) {
               if ( i.isIndexForFields(Arrays.asList("systemRefId"))) {
                   if (i.isUnique()) {
                       indexOperations.dropIndex(i.getName());
                   }
               }
            }
            indexOperations.ensureIndex(new Index().on("systemRefId", Sort.Direction.ASC));
            indexOperations.ensureIndex(new Index().on("context", Sort.Direction.ASC));
            indexOperations.ensureIndex(new Index().on("eol", Sort.Direction.ASC));

            log.info("created index for AssetMongoEntity");
        } else {
            log.debug("disabled creating of index for AssetMongoEntity");
        }
    }

}
