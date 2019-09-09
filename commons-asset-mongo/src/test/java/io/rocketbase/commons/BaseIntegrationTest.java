package io.rocketbase.commons;


import io.rocketbase.commons.model.AssetMongoEntity;
import lombok.Getter;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
public abstract class BaseIntegrationTest {

    @Getter
    @Value("http://localhost:${local.server.port}")
    protected String baseUrl;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private GridFsTemplate gridFsTemplate;

    @Before
    public void cleanUpBefore() {
        gridFsTemplate.delete(new Query());
        mongoTemplate.findAllAndRemove(new Query(), AssetMongoEntity.class);
    }
}
