package io.rocketbase.commons.service;

import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.model.AssetMongoEntity;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AssetRepositoryTest extends BaseIntegrationTest {

    private final Pageable pageable = PageRequest.of(0, 10);
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private AssetRepository assetRepository;

    @Before
    public void beforeTest() {
        mongoTemplate.save(AssetMongoEntity.builder()
                .type(AssetType.JPEG)
                .originalFilename("oRiginalF.jpg")
                .created(LocalDateTime.of(2018, 3, 1, 10, 22).toInstant(ZoneOffset.UTC))
                .fileSize(1234L)
                .build());
        mongoTemplate.save(AssetMongoEntity.builder()
                .type(AssetType.JPEG)
                .originalFilename("second.jpg")
                .created(LocalDateTime.of(2018, 2, 20, 10, 30).toInstant(ZoneOffset.UTC))
                .fileSize(1234L)
                .build());
        mongoTemplate.save(AssetMongoEntity.builder()
                .type(AssetType.PDF)
                .originalFilename("second-doc.pdf")
                .referenceUrl("http://www.other.com/second-doc.pdf")
                .created(LocalDateTime.of(2018, 3, 3, 11, 0).toInstant(ZoneOffset.UTC))
                .fileSize(2345L)
                .build());
        mongoTemplate.save(AssetMongoEntity.builder()
                .type(AssetType.PDF)
                .originalFilename("sample.pdf")
                .referenceUrl("http://www.rocketbase.io/sample.pdf")
                .created(LocalDateTime.of(2018, 4, 11, 2, 0).toInstant(ZoneOffset.UTC))
                .fileSize(2345L)
                .build());
    }

    @Test
    public void findAllByType() {
        // given
        QueryAsset query = QueryAsset.builder()
                .type(AssetType.JPEG)
                .type(AssetType.PNG)
                .build();
        // when
        Page<AssetEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(2));
        Set<AssetType> assetTypes = page.getContent().stream().map(AssetEntity::getType).collect(Collectors.toSet());
        assertThat(assetTypes.size(), equalTo(1));
        assertThat(assetTypes.iterator().next(), equalTo(AssetType.JPEG));
    }

    @Test
    public void findAllByCreatedBefore() {
        // given
        QueryAsset query = QueryAsset.builder()
                .before(LocalDateTime.of(2018, 3, 1, 9, 0).toInstant(ZoneOffset.UTC))
                .build();
        // when
        Page<AssetEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(1));
    }

    @Test
    public void findAllByCreatedAfter() {
        // given
        QueryAsset query = QueryAsset.builder()
                .after(LocalDateTime.of(2018, 3, 1, 9, 0).toInstant(ZoneOffset.UTC))
                .build();
        // when
        Page<AssetEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(3));
    }

    @Test
    public void findAllByCreatedBeforeAndAfter() {
        // given
        QueryAsset query = QueryAsset.builder()
                .before(LocalDateTime.of(2018, 3, 20, 23, 59).toInstant(ZoneOffset.UTC))
                .after(LocalDateTime.of(2018, 3, 1, 0, 0).toInstant(ZoneOffset.UTC))
                .build();
        // when
        Page<AssetEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(2));
    }

    @Test
    public void findAllByOriginalFilename() {
        // given
        QueryAsset query = QueryAsset.builder()
                .originalFilename("rigina")
                .build();
        // when
        Page<AssetEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(1));
    }

    @Test
    public void findAllByReferenceUrl() {
        // given
        QueryAsset query = QueryAsset.builder()
                .referenceUrl("www.rocketbase")
                .build();
        // when
        Page<AssetEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(1));
    }

    @Test
    public void findAllCombined() {
        // given
        QueryAsset query = QueryAsset.builder()
                .before(LocalDateTime.of(2018, 3, 20, 23, 59).toInstant(ZoneOffset.UTC))
                .after(LocalDateTime.of(2018, 3, 1, 0, 0).toInstant(ZoneOffset.UTC))
                .type(AssetType.JPEG)
                .type(AssetType.PNG)
                .originalFilename("ORIGINAL")
                .build();
        // when
        Page<AssetEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(1));
    }
}