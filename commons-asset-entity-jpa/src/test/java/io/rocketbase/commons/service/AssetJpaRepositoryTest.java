package io.rocketbase.commons.service;

import io.rocketbase.commons.Application;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.model.AssetJpaEntity;
import io.rocketbase.commons.repository.AssetEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;
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

    @Before
    public void beforeTest() {
        repository.deleteAll();

        assetRepository.save(AssetJpaEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(Instant.now())
                .type(AssetType.JPEG)
                .originalFilename("oRiginalF.jpg")
                .created(LocalDateTime.of(2018, 3, 1, 10, 22).toInstant(ZoneOffset.UTC))
                .fileSize(1234L)
                .build());
        assetRepository.save(AssetJpaEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(Instant.now())
                .type(AssetType.JPEG)
                .originalFilename("second.jpg")
                .created(LocalDateTime.of(2018, 2, 20, 10, 30).toInstant(ZoneOffset.UTC))
                .fileSize(1234L)
                .build());
        assetRepository.save(AssetJpaEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(Instant.now())
                .type(AssetType.PDF)
                .originalFilename("second-doc.pdf")
                .referenceUrl("http://www.other.com/second-doc.pdf")
                .created(LocalDateTime.of(2018, 3, 3, 11, 0).toInstant(ZoneOffset.UTC))
                .fileSize(2345L)
                .build());
        assetRepository.save(AssetJpaEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(Instant.now())
                .type(AssetType.PDF)
                .originalFilename("sample.pdf")
                .referenceUrl("http://www.rocketbase.io/sample.pdf")

                .created(LocalDateTime.of(2018, 4, 11, 2, 0).toInstant(ZoneOffset.UTC))
                .fileSize(2345L)
                .build());
        assetRepository.save(AssetJpaEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(Instant.now())
                .type(AssetType.PDF)
                .originalFilename("eol.pdf")
                .created(LocalDateTime.of(2019, 12, 31, 11, 0).toInstant(ZoneOffset.UTC))
                .eol(Instant.now().minus(5, ChronoUnit.MINUTES))
                .fileSize(4567L)
                .build());
        assetRepository.save(AssetJpaEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(Instant.now())
                .type(AssetType.JPEG)
                .originalFilename("should-expire-future.jpg")
                .created(LocalDateTime.of(2020, 1, 14, 13, 30).toInstant(ZoneOffset.UTC))
                .eol(Instant.now().plus(10, ChronoUnit.DAYS))
                .fileSize(1568L)
                .build());
        assetRepository.save(AssetJpaEntity.builder()
                .id(UUID.randomUUID().toString())
                .created(Instant.now())
                .type(AssetType.GIF)
                .originalFilename("expired.gif")
                .created(LocalDateTime.of(2020, 2, 2, 22, 30).toInstant(ZoneOffset.UTC))
                .eol(Instant.now().minus(10, ChronoUnit.DAYS))
                .fileSize(968L)
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
        Page<AssetJpaEntity> page = assetRepository.findAll(query, pageable);

        // then
        assertThat(page, notNullValue());
        assertThat(page.getNumberOfElements(), equalTo(3));
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

}