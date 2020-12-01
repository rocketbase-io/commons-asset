package io.rocketbase.commons.dto.asset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsiveImage implements Serializable {

    /**
     * layout example: (max-width: 640px) 100vw, 640px<br>
     * max-width represents the tallest preview-with that is available (with given {@link PreviewSize} filters)
     */
    private String sizes;

    /**
     * layout example: https://preview/abc_w_300.png 300w, https://preview/abc_w_600.png 600w,  https://preview/abc_w.png 640w
     */
    private String srcset;

    /**
     * contains the talles preview url as default src (browser will detect best fitting preview from srcset)
     */
    private String src;

}
