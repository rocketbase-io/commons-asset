package io.rocketbase.commons.service.handler;

import io.rocketbase.commons.BaseIntegrationTest;
import io.rocketbase.commons.dto.asset.PreviewSize;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
public class DefaultAssetHandlerIntegrationTest extends BaseIntegrationTest {

    @Resource
    private AssetHandler assetHandler;

    @Test
    public void correctConfiguration() throws Exception {
        // given
        DefaultJavaAssetHandler casted = (DefaultJavaAssetHandler) assetHandler;

        // when
        Map<PreviewSize, Float> qualityMap = casted.config.getPreviewQuality();

        // then
        assertThat(qualityMap, notNullValue());
        assertThat(qualityMap.get(PreviewSize.XS), equalTo(PreviewSize.XS.getDefaultQuality()));
        assertThat(qualityMap.get(PreviewSize.S), equalTo(0.5f));
        assertThat(qualityMap.get(PreviewSize.XL), equalTo(PreviewSize.XL.getDefaultQuality()));
    }
}