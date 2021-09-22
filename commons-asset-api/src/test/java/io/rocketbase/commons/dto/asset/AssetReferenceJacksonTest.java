package io.rocketbase.commons.dto.asset;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AssetReferenceJacksonTest {


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .findAndRegisterModules();


    @Test
    public void serialize() throws JsonProcessingException {
        String result = buildJson();
        assertThat(result, notNullValue());
    }

    @Test
    public void deserialize() throws JsonProcessingException {
        AssetReferenceHolder result = OBJECT_MAPPER.readValue(buildJson(), AssetReferenceHolder.class);
        assertThat(result, notNullValue());
    }

    @Test
    public void deserializeAssetRead() throws JsonProcessingException {
        Instant now = Instant.now();
        String download = "https://download.com";
        String assetReadString= OBJECT_MAPPER.writeValueAsString(AssetRead.builderRead()
                        .eol(now)
                        .download(download)
                        .meta(buildMeta())
                .build());
        AssetRead assetRead = OBJECT_MAPPER.readValue(assetReadString, AssetRead.class);
        assertThat(assetRead, notNullValue());
        assertThat(assetRead.getEol().truncatedTo(ChronoUnit.SECONDS), equalTo(now.truncatedTo(ChronoUnit.SECONDS)));
        assertThat(assetRead.getDownload(), equalTo(download));
        assertThat(assetRead.getMeta(), equalTo(buildMeta()));
    }

    private String buildJson() throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(AssetReferenceHolder.builder()
                .id("id")
                .assetReference(DefaultAssetReference.builder()
                        .id("123")
                        .type(AssetType.JPEG)
                        .meta(buildMeta())
                        .context("contect")
                        .build())
                .build());
    }

    private AssetMeta buildMeta() {
        return AssetMeta.builder()
                .resolution(new Resolution(600,400))
                .referenceUrl("https://sample.com/sample.jpg")
                .colorPalette(ColorPalette.builder()
                        .primary("ff00ff")
                        .colors(Arrays.asList("ff0000", "000000"))
                        .build())
                .build();
    }


    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class AssetReferenceHolder {
        private String id;
        @Singular
        private List<AssetReference> assetReferences;
    }

}