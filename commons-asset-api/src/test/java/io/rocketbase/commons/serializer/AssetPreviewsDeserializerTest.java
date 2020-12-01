package io.rocketbase.commons.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rocketbase.commons.dto.asset.AssetPreviews;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.dto.asset.ResponsiveImage;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AssetPreviewsDeserializerTest {

    @Test
    public void deserializeWithoutResponsive() throws Exception {
        // given
        String jsonString = "{\"xs\":\"http://xs\"}";
        // when
        ObjectMapper objectMapper = new ObjectMapper();
        AssetPreviews response = objectMapper.readValue(jsonString, AssetPreviews.class);
        // then
        assertThat(response, notNullValue());
        assertThat(response.getPreview(PreviewSize.XS), equalTo("http://xs"));
        assertThat(response.getResponsive(), nullValue());
    }

    @Test
    public void deserializeIgnoreInvalidPreviewSize() throws Exception {
        // given
        String jsonString = "{\"xs\":\"http://xs\",\"z\":\"http://xs\"}";
        // when
        ObjectMapper objectMapper = new ObjectMapper();
        AssetPreviews response = objectMapper.readValue(jsonString, AssetPreviews.class);
        // then
        assertThat(response, notNullValue());
        assertThat(response.getPreview(PreviewSize.XS), equalTo("http://xs"));
        assertThat(response.getResponsive(), nullValue());
    }

    @Test
    public void deserializeWithResponsive() throws Exception {
        // given
        String jsonString = "{\"xs\":\"http://xs\",\"responsive\":{\"src\":\"src\",\"srcset\":\"srcset\",\"sizes\":\"sizes\"}}";
        // when
        ObjectMapper objectMapper = new ObjectMapper();
        AssetPreviews response = objectMapper.readValue(jsonString, AssetPreviews.class);
        // then
        assertThat(response, notNullValue());
        assertThat(response.getPreview(PreviewSize.XS), equalTo("http://xs"));
        ResponsiveImage responsive = response.getResponsive();
        assertThat(responsive, notNullValue());
        assertThat(responsive.getSrc(), equalTo("src"));
        assertThat(responsive.getSrcset(), equalTo("srcset"));
        assertThat(responsive.getSizes(), equalTo("sizes"));
    }
}