package io.rocketbase.commons;


import io.rocketbase.commons.model.AssetEntity;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import javax.annotation.Resource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    @Getter
    @Value("http://localhost:${local.server.port}")
    protected String baseUrl;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private GridFsTemplate gridFsTemplate;

    @BeforeEach
    public void cleanUpBefore() {
        gridFsTemplate.delete(new Query());
        mongoTemplate.findAllAndRemove(new Query(), AssetEntity.class);
    }

}
