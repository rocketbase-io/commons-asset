package io.rocketbase.commons.dto.asset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.annotation.Nullable;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "calculated Responsive Image Breakpoints")
public class ResponsiveImage implements Serializable {

    /**
     * layout example: (max-width: 640px) 100vw, 640px<br>
     * max-width represents the tallest preview-with that is available (with given {@link PreviewSize} filters)
     */
    @Nullable
    @Schema(example = "(max-width: 640px) 100vw, 640px")
    private String sizes;

    /**
     * layout example: https://preview/abc_w_300.png 300w, https://preview/abc_w_600.png 600w,  https://preview/abc_w.png 640w
     */
    @Nullable
    @Schema(example = "https://preview/abc_w_300.png 300w, https://preview/abc_w_600.png 600w,  https://preview/abc_w.png 640w")
    private String srcset;

    /**
     * contains the tallest preview url as default src (browser will detect best fitting preview from srcset)
     */
    @Schema(description = "contains the tallest preview url as default src (browser will detect best fitting preview from srcset)")
    private String src;

}
