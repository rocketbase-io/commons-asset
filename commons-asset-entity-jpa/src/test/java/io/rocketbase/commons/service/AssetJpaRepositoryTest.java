package io.rocketbase.commons.service;

import io.rocketbase.commons.Application;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.model.AssetJpaEntity;
import io.rocketbase.commons.model.AssetJpaKeyValueJpaEntity;
import io.rocketbase.commons.repository.AssetEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AssetJpaRepositoryTest {

    private final Pageable pageable = PageRequest.of(0, 10);

    @Resource
    private AssetRepository<AssetJpaEntity> assetRepository;

    @Resource
    private AssetEntityRepository repository;

    protected AssetJpaEntity buildKeyValues(AssetJpaEntity entity, Pair<String, String>... values) {
        List<AssetJpaKeyValueJpaEntity> keyValues = new ArrayList<>();
        for (Pair<String, String> v : values) {
            keyValues.add(new AssetJpaKeyValueJpaEntity(entity, v.getFirst(), v.getSecond()));
        }
        entity.setKeyValues(keyValues);
        return entity;
    }

    @Before
    public void beforeTest() {
        repository.deleteAll();


        assetRepository.save(buildKeyValues(AssetJpaEntity.builder()
                        .type(AssetType.JPEG)
                        .originalFilename("oRiginalF.jpg")
                        .created(LocalDateTime.of(2018, 3, 1, 10, 22).toInstant(ZoneOffset.UTC))
                        .fileSize(1234L)
                        .build(),
                Pair.of("_test", "v"), Pair.of("user", "123"))
        );
        assetRepository.save(buildKeyValues(AssetJpaEntity.builder()
                        .type(AssetType.JPEG)
                        .originalFilename("second.jpg")
                        .created(LocalDateTime.of(2018, 2, 20, 10, 30).toInstant(ZoneOffset.UTC))
                        .fileSize(1234L)
                        .build(),
                Pair.of("_test", "v"), Pair.of("user", "123"), Pair.of("extra", "1")));
        assetRepository.save(buildKeyValues(AssetJpaEntity.builder()
                        .type(AssetType.PDF)
                        .originalFilename("second-doc.pdf")
                        .referenceUrl("http://www.other.com/second-doc.pdf")
                        .created(LocalDateTime.of(2018, 3, 3, 11, 0).toInstant(ZoneOffset.UTC))
                        .fileSize(2345L)
                        .build(),
                Pair.of("_test", "v"), Pair.of("user", "123")));
        assetRepository.save(buildKeyValues(AssetJpaEntity.builder()
                        .type(AssetType.PDF)
                        .originalFilename("sample.pdf")
                        .referenceUrl("http://www.rocketbase.io/sample.pdf")
                        .created(LocalDateTime.of(2018, 4, 11, 2, 0).toInstant(ZoneOffset.UTC))
                        .fileSize(2345L)
                        .build(),
                Pair.of("_test", "v"), Pair.of("user", "123")));
        assetRepository.save(buildKeyValues(AssetJpaEntity.builder()
                        .type(AssetType.PDF)
                        .originalFilename("eol.pdf")
                        .created(LocalDateTime.of(2019, 12, 31, 11, 0).toInstant(ZoneOffset.UTC))
                        .eol(Instant.now().minus(5, ChronoUnit.MINUTES))
                        .fileSize(4567L)
                        .build(),
                Pair.of("_test", "v"), Pair.of("user", "MoP")));
        assetRepository.save(buildKeyValues(AssetJpaEntity.builder()
                        .type(AssetType.JPEG)
                        .originalFilename("should-expire-future.jpg")
                        .created(LocalDateTime.of(2020, 1, 14, 13, 30).toInstant(ZoneOffset.UTC))
                        .eol(Instant.now().plus(10, ChronoUnit.DAYS))
                        .fileSize(1568L)
                        .build(),
                Pair.of("_test", "v"), Pair.of("user", "mop")));
        assetRepository.save(buildKeyValues(AssetJpaEntity.builder()
                        .type(AssetType.GIF)
                        .originalFilename("expired.gif")
                        .created(LocalDateTime.of(2020, 2, 2, 22, 30).toInstant(ZoneOffset.UTC))
                        .eol(Instant.now().minus(10, ChronoUnit.DAYS))
                        .fileSize(968L)
                        .build(),
                Pair.of("_test", "v"), Pair.of("user", "MOP")));
    }

    @Test
    @Transactional
    public void testOnPrePersistUpdate() {
        repository.findAll()
                .forEach(v -> {
                    assertThat(v.getId(), notNullValue());
                    assertThat(v.getCreated(), notNullValue());
                    if (v.getKeyValues() != null) {
                        for (AssetJpaKeyValueJpaEntity e : v.getKeyValueEntities()) {
                            assertThat(e.getFieldValueHash(), notNullValue());
                            assertThat(e.getLastUpdate(), notNullValue());
                        }
                    }
                });
    }

    @Test
    public void findAllByType() {
        // given
        QueryAsset query = QueryAsset.builder()
                .type(AssetType.JPEG)
                .type(AssetType.PNG)
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
                .type(AssetType.JPEG)
                .type(AssetType.PNG)
                .originalFilename("ORIGINAL")
                .keyValue("_test", "v")
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
                .type(AssetType.JPEG)
                .type(AssetType.PDF)
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
                .keyValue("extra", "1")
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
                .keyValue("user", "MOp")
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
        AssetJpaEntity entity = assetRepository.save(buildKeyValues(AssetJpaEntity.builder()
                        .type(AssetType.JPEG)
                        .originalFilename("second.jpg")
                        .created(LocalDateTime.of(2018, 2, 20, 10, 30).toInstant(ZoneOffset.UTC))
                        .fileSize(1234L)
                        .build(),
                Pair.of("_test", "v"), Pair.of("user", "123"), Pair.of("extra", "1")));

        // when
        entity.removeKeyValue("extra");
        assetRepository.save(entity);
        entity = assetRepository.findById(entity.getId()).get();

        // then
        assertThat(entity.hasKeyValue("extra"), equalTo(false));
        assertThat(entity.getKeyValueEntities().size(), equalTo(2));
    }

    @Test
    public void checkKeyValueLastModified() throws InterruptedException {
        // given
        AssetJpaEntity entity = assetRepository.save(buildKeyValues(AssetJpaEntity.builder()
                        .type(AssetType.JPEG)
                        .originalFilename("second.jpg")
                        .created(LocalDateTime.of(2018, 2, 20, 10, 30).toInstant(ZoneOffset.UTC))
                        .fileSize(1234L)
                        .build(),
                Pair.of("_test", "v"), Pair.of("user", "123"), Pair.of("extra", "1")));

        // when
        Thread.sleep(101);
        entity.addKeyValue("user", "345");
        assetRepository.save(entity);

        entity = assetRepository.findById(entity.getId()).get();

        // then
        assertThat(entity.getKeyValueEntities().size(), equalTo(3));
        assertThat(entity.findKeyValue("user").get().getLastUpdate(),
                greaterThan(entity.findKeyValue("_test").get().getLastUpdate()));

    }

}