package io.rocketbase.commons.service;

import com.google.common.collect.ImmutableMap;
import io.rocketbase.commons.Application;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.model.AssetJpaEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class AssetJpaRepositoryTest implements InitializingBean {

    private final Pageable pageable = PageRequest.of(0, 10);

    @Resource
    private AssetRepository<AssetJpaEntity> assetRepository;

    @Resource
    private EntityManager entityManager;

    @Resource
    private ReferenceHashMigrationService referenceHashMigrationService;

    private SimpleJpaRepository<AssetJpaEntity, String> repository;

    @Override
    public void afterPropertiesSet() throws Exception {
        repository = new SimpleJpaRepository<>(AssetJpaEntity.class, entityManager);
    }

    @Before
    public void beforeTest() {
        repository.deleteAll();

        assetRepository.save(AssetJpaEntity.builder()
                .id("abb1b2a3-8bb7-4d7d-bc3f-fbf3118a5633" )
                .type(AssetType.JPEG)
                .originalFilename("oRiginalF.jpg")
                .created(LocalDateTime.of(2018, 3, 1, 10, 22).toInstant(ZoneOffset.UTC))
                .keyValueMap(ImmutableMap.of("_test", "v","user", "123"))
                .fileSize(1234L)
                .build());
        assetRepository.save(AssetJpaEntity.builder()
                .id("d70b671e-fb9e-4260-94be-826d227f4fdb")
                .type(AssetType.JPEG)
                .originalFilename("second.jpg")
                .created(LocalDateTime.of(2018, 2, 20, 10, 30).toInstant(ZoneOffset.UTC))
                .keyValueMap(ImmutableMap.of("_test", "v","user", "123", "extra", "1"))
                .fileSize(1234L)
                .build());
        assetRepository.save(AssetJpaEntity.builder()
                .id("95a8b1af-ec0d-402c-8f2a-00ccae51ba5f")
                .type(AssetType.PDF)
                .originalFilename("second-doc.pdf")
                .referenceUrl("http://www.other.com/second-doc.pdf")
                .created(LocalDateTime.of(2018, 3, 3, 11, 0).toInstant(ZoneOffset.UTC))
                .keyValueMap(ImmutableMap.of("_test", "v","user", "123"))
                .fileSize(2345L)
                .build());
        assetRepository.save(AssetJpaEntity.builder()
                .id("b2ae9cb4-92dd-4158-81f4-82808c3b2d54")
                .type(AssetType.PDF)
                .originalFilename("sample.pdf")
                .referenceUrl("http://www.rocketbase.io/sample.pdf")
                .created(LocalDateTime.of(2018, 4, 11, 2, 0).toInstant(ZoneOffset.UTC))
                .keyValueMap(ImmutableMap.of("_test", "v","user", "123"))
                .fileSize(2345L)
                .build());
        assetRepository.save(AssetJpaEntity.builder()
                .id("a0489b53-9512-4e46-97cc-1f7597c828ec")
                .type(AssetType.PDF)
                .originalFilename("eol.pdf")
                .created(LocalDateTime.of(2019, 12, 31, 11, 0).toInstant(ZoneOffset.UTC))
                .keyValueMap(ImmutableMap.of("_test", "v","user", "MoP"))
                .eol(Instant.now().minus(5, ChronoUnit.MINUTES))
                .fileSize(4567L)
                .build());
        assetRepository.save(AssetJpaEntity.builder()
                .id("282ac4d6-6ece-43d1-b9e9-d032ddb59799")
                .type(AssetType.JPEG)
                .originalFilename("should-expire-future.jpg")
                .created(LocalDateTime.of(2020, 1, 14, 13, 30).toInstant(ZoneOffset.UTC))
                .keyValueMap(ImmutableMap.of("_test", "v","user", "mop"))
                .eol(Instant.now().plus(10, ChronoUnit.DAYS))
                .fileSize(1568L)
                .build());
        assetRepository.save(AssetJpaEntity.builder()
                .id("c24f5c69-7e9e-478c-aacd-0d6506380a2c")
                .type(AssetType.GIF)
                .originalFilename("expired.gif")
                .created(LocalDateTime.of(2020, 2, 2, 22, 30).toInstant(ZoneOffset.UTC))
                .keyValueMap(ImmutableMap.of("_test", "v","user", "MOP"))
                .eol(Instant.now().minus(10, ChronoUnit.DAYS))
                .fileSize(968L)
                .build());

        referenceHashMigrationService.generateHashesForReferenceUrls(10);
    }

    @Test
    public void findAllByType() {
        // given
        QueryAsset query = QueryAsset.builder()
                .types(Arrays.asList(AssetType.JPEG, AssetType.PNG))
                .build();
        // when
        Page<AssetJpaEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(3));
        assertThat(page.getContent().stream().filter(v -> v.hasKeyValue("_test")).count(), equalTo(3L));
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
        Page<AssetJpaEntity> page = assetRepository.findAll(query, pageable);

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
        Page<AssetJpaEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(6));
        assertThat(page.getContent().stream().filter(v -> v.hasKeyValue("_test")).count(), equalTo(6L));
    }

    @Test
    public void findAllByCreatedBeforeAndAfter() {
        // given
        QueryAsset query = QueryAsset.builder()
                .before(LocalDateTime.of(2018, 3, 20, 23, 59).toInstant(ZoneOffset.UTC))
                .after(LocalDateTime.of(2018, 3, 1, 0, 0).toInstant(ZoneOffset.UTC))
                .build();
        // when
        Page<AssetJpaEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(2));
        assertThat(page.getContent().stream().filter(v -> v.hasKeyValue("_test")).count(), equalTo(2L));
    }

    @Test
    public void findAllByOriginalFilename() {
        // given
        QueryAsset query = QueryAsset.builder()
                .originalFilename("rigina")
                .build();
        // when
        Page<AssetJpaEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(1));
    }

    @Test
    public void findAllByReferenceUrl() {
        // given
        QueryAsset query = QueryAsset.builder()
                .referenceUrl("http://www.rocketbase.io/sample.pdf")
                .build();
        // when
        Page<AssetJpaEntity> page = assetRepository.findAll(query, pageable);

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
                .types(Arrays.asList(AssetType.JPEG, AssetType.PNG))
                .originalFilename("ORIGINAL")
                .keyValues(ImmutableMap.of("_test", "v"))
                .build();
        // when
        Page<AssetJpaEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(1));
    }

    @Test
    public void findHasEol() {
        // given
        QueryAsset query = QueryAsset.builder()
                .hasEolValue(true)
                .build();
        // when
        Page<AssetJpaEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(3));
        assertThat(page.getContent().get(0).getEol(), notNullValue());
        assertThat(page.getContent().get(1).getEol(), notNullValue());
        assertThat(page.getContent().get(2).getEol(), notNullValue());
    }

    @Test
    public void findEol() {
        // given
        QueryAsset query = QueryAsset.builder()
                .isEol(true)
                .build();
        // when
        Page<AssetJpaEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(2));
        assertThat(page.getContent().get(0).getEol(), notNullValue());
        assertThat(page.getContent().get(0).getEol(), lessThan(Instant.now()));
        assertThat(page.getContent().get(1).getEol(), notNullValue());
        assertThat(page.getContent().get(1).getEol(), lessThan(Instant.now()));
    }

    @Test
    public void findCombinedEol() {
        // given
        QueryAsset query = QueryAsset.builder()
                .after(LocalDateTime.of(2018, 3, 1, 9, 0).toInstant(ZoneOffset.UTC))
                .types(Arrays.asList(AssetType.JPEG, AssetType.PDF))
                .isEol(true)
                .build();
        // when
        Page<AssetJpaEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(1));
        assertThat(page.getContent().get(0).getEol(), notNullValue());
        assertThat(page.getContent().get(0).getEol(), lessThan(Instant.now()));
    }

    @Test
    public void findKeyValueExtra() {
        // given
        QueryAsset query = QueryAsset.builder()
                .keyValues(ImmutableMap.of("extra", "1"))
                .build();
        // when
        Page<AssetJpaEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(1));
        assertThat(page.getContent().get(0).hasKeyValue("extra"), equalTo(true));
    }

    @Test
    public void findKeyValueCaseInsensitive() {
        // given
        QueryAsset query = QueryAsset.builder()
                .keyValues(ImmutableMap.of("user", "MOp"))
                .build();
        // when
        Page<AssetJpaEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(3));
        assertThat(page.getContent().stream().filter(v -> v.getKeyValue("user").equalsIgnoreCase("mop")).count(), equalTo(3L));
    }

    @Test
    public void checkKeyValueRemoveAfterPersist() {
        // given
        AssetJpaEntity entity = assetRepository.save(AssetJpaEntity.builder()
                .id("1fa60efd-cf5b-4fb0-a9b9-d6d833e765e1" )
                .type(AssetType.JPEG)
                .originalFilename("second.jpg")
                .created(LocalDateTime.of(2018, 2, 20, 10, 30).toInstant(ZoneOffset.UTC))
                .fileSize(1234L)
                .keyValueMap(ImmutableMap.of("_test", "v", "user", "123", "extra", "1"))
                .build());

        // when
        entity.removeKeyValue("extra");
        assetRepository.save(entity);
        entity = assetRepository.findById(entity.getId()).get();

        // then
        assertThat(entity.hasKeyValue("extra"), equalTo(false));
        assertThat(entity.getKeyValues().size(), equalTo(2));
    }
}