package io.rocketbase.commons.service;

import io.rocketbase.commons.Application;
import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.model.AssetJpaEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AssetJpaRepositoryTest {

    @Resource
    private AssetRepository<AssetJpaEntity> service;

    @Resource
    private ReferenceHashMigrationService referenceHashMigrationService;

    @Test
    public void findAllNullQuery() {
        // given
        QueryAsset query = null;

        // when
        Page<AssetJpaEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(22L));
    }

    @Test
    public void findAllQueryReferenceUrl() {
        // given
        String referenceUrl = "http://some-addresse.io/download-file?id=123";
        QueryAsset query = QueryAsset.builder()
                .referenceUrl(referenceUrl)
                .build();

        // when
        Page<AssetJpaEntity> result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(0L));

        // after migration
        referenceHashMigrationService.generateHashesForReferenceUrls(5);

        // when
        result = service.findAll(query, PageRequest.of(0, 10));

        // then
        assertThat(result, notNullValue());
        assertThat(result.getTotalElements(), equalTo(1L));
        assertThat(result.getContent().get(0).getReferenceUrl(), equalTo(referenceUrl));
    }

    @Test
    public void findByIdOrSystemRefIdWithRef() {
        // given
        String sysRefId = "sys-ref-1";

        // when
        Optional<AssetJpaEntity> optional = service.findByIdOrSystemRefId(sysRefId);

        // then
        assertThat(optional, notNullValue());
        assertThat(optional.isPresent(), equalTo(true));
        assertThat(optional.get().getId(), equalTo("5a1d5e9df19aec0001815d3b"));
    }

    @Test
    public void findByIdOrSystemRefIdWithId() {
        // given
        String id = "5a1d5e9df19aec0001815d3b";

        // when
        Optional<AssetJpaEntity> optional = service.findByIdOrSystemRefId(id);

        // then
        assertThat(optional, notNullValue());
        assertThat(optional.isPresent(), equalTo(true));
        assertThat(optional.get().getSystemRefId(), equalTo("sys-ref-1"));
    }

    @Test
    public void findById() {
        // given
        String id = "5a1d5e9df19aec0001815d3b";

        // when
        Optional<AssetJpaEntity> optional = service.findById(id);

        // then
        assertThat(optional, notNullValue());
        assertThat(optional.isPresent(), equalTo(true));
        assertThat(optional.get().getSystemRefId(), equalTo("sys-ref-1"));
    }

    @Test
    public void findByIdInvalid() {
        // given
        String id = "sys-ref-1";

        // when
        Optional<AssetJpaEntity> optional = service.findById(id);

        // then
        assertThat(optional, notNullValue());
        assertThat(optional.isPresent(), equalTo(false));
    }

    @Test
    public void testKeyValues() {
        // given
        String id = "5a1d5e9df19aec0001815d3b";

        // when
        AssetJpaEntity asset = service.findById(id).orElseThrow(NoClassDefFoundError::new);

        // then
        assertThat(asset.getKeyValueMap(), notNullValue());
        assertThat(asset.getKeyValueMap().containsKey("client"), equalTo(true));
        assertThat(asset.getKeyValueMap().get("client"), equalTo("1"));
        assertThat(asset.getKeyValueMap().containsKey("_hidden"), equalTo(true));
        assertThat(asset.getKeyValueMap().get("_hidden"), equalTo("secret"));
    }

}