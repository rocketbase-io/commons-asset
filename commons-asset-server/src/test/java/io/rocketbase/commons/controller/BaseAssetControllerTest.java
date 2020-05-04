package io.rocketbase.commons.controller;

import io.rocketbase.commons.dto.asset.PreviewSize;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.notNullValue;

public class BaseAssetControllerTest {

    @Test
    public void getPreviewSizesSimple() {
        // given
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("size", Arrays.asList("s", "M", "XL"));
        // when
        List<PreviewSize> previewSizes = new SampleBaseAssetControllerTest().getPreviewSizes(map);
        // then
        assertThat(previewSizes, notNullValue());
        assertThat(previewSizes, containsInAnyOrder(PreviewSize.S, PreviewSize.M, PreviewSize.XL));
    }

    @Test
    public void getPreviewSizesCommaseparated() {
        // given
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("size", Arrays.asList("s,M", "XS"));
        // when
        List<PreviewSize> previewSizes = new SampleBaseAssetControllerTest().getPreviewSizes(map);
        // then
        assertThat(previewSizes, notNullValue());
        assertThat(previewSizes, containsInAnyOrder(PreviewSize.S, PreviewSize.M, PreviewSize.XS));
    }


    private static class SampleBaseAssetControllerTest implements BaseAssetController {
    }
}