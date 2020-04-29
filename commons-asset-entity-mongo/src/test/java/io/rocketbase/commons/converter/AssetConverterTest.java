package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.dto.asset.*;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.model.AssetMongoEntity;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

public class AssetConverterTest {

    protected AssetEntity createSample() {
        return AssetMongoEntity.builder()
                .id("1235678")
                .urlPath("12345678")
                .fileSize(1234L)
                .created(Instant.now())
                .originalFilename("original.png")
                .type(AssetType.PNG)
                .systemRefId("123")
                .resolution(new Resolution(100, 200))
                .build();
    }

    @Test
    public void testFromEntityWithLocalRender() {
        // given
        AssetApiProperties assetApiProperties = new AssetApiProperties();
        assetApiProperties.setBaseUrl("http://localhost:8080");

        AssetConverter converter = new AssetConverter(assetApiProperties, new DefaultAssetPreviewService(assetApiProperties));
        // when
        AssetRead assetRead = converter.fromEntity(createSample(), Arrays.asList(PreviewSize.S, PreviewSize.M, PreviewSize.L));

        // then
        assertThat(assetRead, notNullValue());
        assertThat(assetRead.getPreviews(), notNullValue());
        assertThat(assetRead.getPreviews().getPreviewMap().size(), equalTo(3));
        String baseWithApi = assetApiProperties.getBaseUrl() + assetApiProperties.getPath() + "/";
        assertThat(assetRead.getPreviews().getPreviewMap().get(PreviewSize.S), equalTo(baseWithApi + assetRead.getId() + "/s"));
        assertThat(assetRead.getPreviews().getPreviewMap().get(PreviewSize.M), equalTo(baseWithApi + assetRead.getId() + "/m"));
        assertThat(assetRead.getPreviews().getPreviewMap().get(PreviewSize.L), equalTo(baseWithApi + assetRead.getId() + "/l"));
    }

    @Test
    public void testEntityToRead() {
        // given
        AssetEntity entity = createSample();

        AssetApiProperties assetApiProperties = new AssetApiProperties();
        AssetConverter converter = new AssetConverter(assetApiProperties, new DefaultAssetPreviewService(assetApiProperties));
        // when
        AssetRead assetRead = converter.fromEntity(entity);

        // then
        assertThat(assetRead, notNullValue());
        assertThat(assetRead.getId(), equalTo(entity.getId()));
        assertThat(assetRead.getUrlPath(), equalTo(entity.getUrlPath()));
        assertThat(assetRead.getMeta().getFileSize(), equalTo(entity.getFileSize()));
        assertThat(assetRead.getMeta().getCreated(), equalTo(entity.getCreated()));
        assertThat(assetRead.getMeta().getOriginalFilename(), equalTo(entity.getOriginalFilename()));
        assertThat(assetRead.getType(), equalTo(entity.getType()));
        assertThat(assetRead.getSystemRefId(), equalTo(entity.getSystemRefId()));
        assertThat(assetRead.getMeta().getResolution(), equalTo(entity.getResolution()));
        assertThat(assetRead.getLqip(), nullValue());
    }

    @Test
    public void testEntityToReadWithLqip() {
        // given
        AssetEntity entity = createSample();
        entity.setLqip("data:image/jpeg;base64,/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDAHJPVmRWR3JkXWSBeXKIq/+6q52dq//6/8//////////////////////////////////////////////////////2wBDAXmBgauWq/+6uv//////////////////////////////////////////////////////////////////////////wAARCAAyAEsDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwCtRTkRnOFGaspAiDc5B/lQBXSJn6Dj1NTrbIwBy1JJcdox+NQZJ5JNAFn7Knq1H2Vf7xqtn3P50ZPYmgB8kJAJTJUVFViGfaNr9Oxp0sAb5k6+nrQBVooIwcGigB6SOn3T+FDOzn5jTKUe9ABUgHHTimjb3qQkKQDxSGN8rjqaNgUdafvXHf8AKkDK2celAERGKkimMZweV/lTdwIpppiJJ3jfoDn1qKiigBKKKKACpC25QTyQKjpRjvQAoPNKXwpVeh60mRnpSUAJRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQB//Z\n");

        AssetApiProperties assetApiProperties = new AssetApiProperties();
        AssetConverter converter = new AssetConverter(assetApiProperties, new DefaultAssetPreviewService(assetApiProperties));
        // when
        AssetRead assetRead = converter.fromEntity(entity);

        // then
        assertThat(assetRead.getLqip(), notNullValue());
        assertThat(assetRead.getLqip(), equalTo(entity.getLqip()));
    }

    @Test
    public void testEntityToReference() {
        // given
        AssetEntity entity = createSample();

        AssetApiProperties assetApiProperties = new AssetApiProperties();
        AssetConverter converter = new AssetConverter(assetApiProperties, new DefaultAssetPreviewService(assetApiProperties));
        // when
        AssetReference assetReference = converter.fromEntity(entity).toReference();

        // then
        assertThat(assetReference, notNullValue());
        assertThat(assetReference.getId(), equalTo(entity.getId()));
        assertThat(assetReference.getUrlPath(), equalTo(entity.getUrlPath()));
        assertThat(assetReference.getMeta().getFileSize(), equalTo(entity.getFileSize()));
        assertThat(assetReference.getMeta().getCreated(), equalTo(entity.getCreated()));
        assertThat(assetReference.getMeta().getOriginalFilename(), equalTo(entity.getOriginalFilename()));
        assertThat(assetReference.getType(), equalTo(entity.getType()));
        assertThat(assetReference.getSystemRefId(), equalTo(entity.getSystemRefId()));
        assertThat(assetReference.getMeta().getResolution(), equalTo(entity.getResolution()));
        assertThat(assetReference.getLqip(), nullValue());
    }

    @Test
    public void testEntityToReferenceWithLqip() {
        // given
        AssetEntity entity = createSample();
        entity.setLqip("data:image/jpeg;base64,/9j/4AAQSkZJRgABAgAAAQABAAD/2wBDAHJPVmRWR3JkXWSBeXKIq/+6q52dq//6/8//////////////////////////////////////////////////////2wBDAXmBgauWq/+6uv//////////////////////////////////////////////////////////////////////////wAARCAAyAEsDASIAAhEBAxEB/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL/8QAtRAAAgEDAwIEAwUFBAQAAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWmp6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6/8QAHwEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSExBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6/9oADAMBAAIRAxEAPwCtRTkRnOFGaspAiDc5B/lQBXSJn6Dj1NTrbIwBy1JJcdox+NQZJ5JNAFn7Knq1H2Vf7xqtn3P50ZPYmgB8kJAJTJUVFViGfaNr9Oxp0sAb5k6+nrQBVooIwcGigB6SOn3T+FDOzn5jTKUe9ABUgHHTimjb3qQkKQDxSGN8rjqaNgUdafvXHf8AKkDK2celAERGKkimMZweV/lTdwIpppiJJ3jfoDn1qKiigBKKKKACpC25QTyQKjpRjvQAoPNKXwpVeh60mRnpSUAJRRRQAUUUUAFFFFABRRRQAUUUUAFFFFABRRRQB//Z\n");

        AssetApiProperties assetApiProperties = new AssetApiProperties();
        AssetConverter converter = new AssetConverter(assetApiProperties, new DefaultAssetPreviewService(assetApiProperties));
        // when
        AssetReference assetReference = converter.fromEntity(entity).toReference();

        // then
        assertThat(assetReference.getLqip(), notNullValue());
        assertThat(assetReference.getLqip(), equalTo(entity.getLqip()));
    }


}