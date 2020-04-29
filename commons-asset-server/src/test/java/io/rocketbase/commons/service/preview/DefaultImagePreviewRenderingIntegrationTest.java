package io.rocketbase.commons.service.preview;

import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.service.preview.DefaultImagePreviewRendering;
import io.rocketbase.commons.service.preview.ImagePreviewRendering;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
public class DefaultImagePreviewRenderingIntegrationTest extends BaseIntegrationTest {

    @Resource
    private ImagePreviewRendering imagePreviewRendering;


    @Test
    public void correctConfiguration() throws Exception {
        // given
        DefaultImagePreviewRendering casted = (DefaultImagePreviewRendering) imagePreviewRendering;

        // when
        Map<PreviewSize, Float> qualityMap = casted.previewQuality;

        // then
        assertThat(qualityMap, notNullValue());
        assertThat(qualityMap.get(PreviewSize.XS), equalTo(PreviewSize.XS.getDefaultQuality()));
        assertThat(qualityMap.get(PreviewSize.S), equalTo(0.5f));
        assertThat(qualityMap.get(PreviewSize.XL), equalTo(PreviewSize.XL.getDefaultQuality()));
    }
}