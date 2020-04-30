package io.rocketbase.commons.service.handler;

import io.rocketbase.commons.dto.asset.PreviewParameter;
import io.rocketbase.commons.dto.asset.PreviewSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetHandlerConfig implements Serializable {

    private boolean detectResolution;

    private boolean detectColors;

    private boolean lqipEnabled;

    private Map<PreviewSize, Float> previewQuality;

    private PreviewParameter lqipPreview;

    public boolean isImageProcessingNeeded() {
        return detectResolution || detectColors || lqipEnabled;
    }

}
