package io.rocketbase.commons.dto.asset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "detection results of colors within an image")
public class ColorPalette implements Serializable {

    /**
     * dominant color
     */
    @Schema(description = "dominant color of photo", example = "#b0b2b5")
    private String primary;

    /**
     * other colors ordered by priority (not containing primary)
     */
    @Schema(description = "other colors ordered by priority (not containing primary)", example = " [ \"#302f2a\", \"#4a5355\" ]")
    private List<String> colors;

    public ColorPalette(ColorPalette other) {
        this.primary = other.primary;
        this.colors = other.colors != null ? new ArrayList<>(other.colors) : null;
    }
}
