package io.rocketbase.commons.service.preview;

import io.rocketbase.commons.dto.asset.PreviewParameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreviewConfig implements Serializable {

    private PreviewParameter previewSize;

    private Integer rotation;

    /**
     * could be rgb or hex
     */
    private String bg;
}
