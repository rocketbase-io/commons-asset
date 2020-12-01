package io.rocketbase.commons.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rocketbase.commons.dto.asset.AssetPreviews;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.dto.asset.ResponsiveImage;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AssetPreviewsSerializerTest {

    @Test
    public void serializeWithoutResponsive() throws Exception {
        // given
        AssetPreviews previews = new AssetPreviews()
                .add(PreviewSize.XS, "http://xs");
        // when
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(previews);
        // then
        assertThat(response, notNullValue());
        assertThat(response, equalTo("{\"xs\":\"http://xs\"}"));
    }

    @Test
    public void serializeWithResponsive() throws Exception {
        // given
        AssetPreviews previews = new AssetPreviews()
                .add(PreviewSize.XS, "http://xs")
                .withResponsive(ResponsiveImage.builder()
                        .src("src")
                        .srcset("srcset")
                        .sizes("sizes")
                        .build());
        // when
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(previews);

        // then
        assertThat(response, notNullValue());
        assertThat(response, equalTo("{\"xs\":\"http://xs\",\"responsive\":{\"sizes\":\"sizes\",\"srcset\":\"srcset\",\"src\":\"src\"}}"));
    }

    @Test
    public void serializeIgnoreEmptyResponsive() throws Exception {
        // given
        AssetPreviews previews = new AssetPreviews()
                .add(PreviewSize.XS, "http://xs")
                .withResponsive(new ResponsiveImage());
        // when
        ObjectMapper objectMapper = new ObjectMapper();
        String response = objectMapper.writeValueAsString(previews);

        // then
        assertThat(response, notNullValue());
        assertThat(response, equalTo("{\"xs\":\"http://xs\"}"));
    }
}