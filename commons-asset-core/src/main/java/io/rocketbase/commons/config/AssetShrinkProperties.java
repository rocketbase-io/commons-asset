package io.rocketbase.commons.config;


import io.rocketbase.commons.dto.asset.PreviewParameter;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "asset.shrink")
/**
 * allows to resize files during upload before storing in filestorage<br>
 * if users uploads a 4320×3240	(14MP) image it should stored not in original size (shrink in advance)<br>
 * by default this feature is disabled
 */
public class AssetShrinkProperties implements Serializable {

    private boolean enabled = false;

    private int maxWidth = 2560;
    private int maxHeight = 2560;
    private float quality = 0.9f;

    public PreviewParameter getPreviewParameter() {
        return new SimplePreviewParameter(maxWidth, maxHeight, quality);
    }

    @Getter
    @RequiredArgsConstructor
    public static class SimplePreviewParameter implements PreviewParameter {

        private final int maxWidth;

        private final int maxHeight;

        private final float defaultQuality;
    }

}
